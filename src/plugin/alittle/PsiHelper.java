package plugin.alittle;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;
import plugin.guess.*;
import plugin.index.ALittleClassData;
import plugin.index.ALittleEnumData;
import plugin.index.ALittleStructData;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.*;
import plugin.reference.ALittleLanguageModifierReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsiHelper {
    // 判断字符串是不是整型值
    public static boolean isInt(@NotNull String content) {
        // 不论值如何，只要包含小数点，那么就不是整数
        if (content.contains(".")) {
            return false;
        }
        return content.startsWith("0x");
    }

    // 计算哈希值
    public static int JSHash(@NotNull String content) {
        byte[] bytes = content.getBytes();
        int l = content.length();
        int h = l;
        int step = (l >> 5) + 1;

        for (int i = l; i >= step; i -= step) {
            h = h ^ ((h << 5) + bytes[i - 1] + (h >> 2));
        }
        return h;
    }

    // 计算结构体的哈希值
    public static int structHash(ALittleGuessStruct guess) {
        return JSHash(guess.namespace_name + "." + guess.struct_name) + JSHash(guess.namespace_name) + JSHash(guess.struct_name);
    }

    // 类的属性类型
    public enum ClassAttrType {
        VAR,            // 成员变量
        FUN,            // 成员函数
        GETTER,         // getter函数
        SETTER,         // setter函数
        STATIC,         // 静态函数
        TEMPLATE,       // 模板参数
    }

    // 访问权限类型
    public enum ClassAccessType {
        PUBLIC,         // 全局可访问
        PROTECTED,      // 本命名域可访问
        PRIVATE,        // 本类可访问
    }

    // 元素类型
    public enum PsiElementType {
        CLASS_NAME,         // 类名
        ENUM_NAME,          // 枚举名
        STRUCT_NAME,        // 结构体名
        INSTANCE_NAME,      // 单例名
        GLOBAL_METHOD,      // 全局函数
        USING_NAME,         // using名
    }

    // 语言判定
    public static boolean isLanguageEnable(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null) {
                ALittleLanguageModifier language = element.getAttributeModifier().getLanguageModifier();
                if (language != null) {
                    PsiReference ref = language.getReference();
                    if (!(ref instanceof ALittleLanguageModifierReference)) return true;
                    ALittleLanguageModifierReference modifier = (ALittleLanguageModifierReference) ref;
                    return !modifier.isLanguageEnable();
                }
            }
        }

        return false;
    }


    // 是否使用原生
    public static boolean isNative(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null) {
                return element.getAttributeModifier().getNativeModifier() != null;
            }
        }

        return false;
    }

    // 检查await
    public static void checkInvokeAwait(PsiElement element) throws ALittleGuessException {
        // 检查这次所在的函数必须要有await或者async修饰
        PsiElement parent = element;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                throw new ALittleGuessException(element, "全局表达式不能调用带有await的函数");
            } else if (parent instanceof ALittleClassCtorDec) {
                throw new ALittleGuessException(element, "构造函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassGetterDec) {
                throw new ALittleGuessException(element, "getter函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassSetterDec) {
                throw new ALittleGuessException(element, "setter函数内不能调用带有await的函数");
            } else if (parent instanceof ALittleClassMethodDec) {
                PsiElement parentElement = parent.getParent();
                if (!(parentElement instanceof ALittleClassElementDec))
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");
                List<ALittleModifier> modifier = ((ALittleClassElementDec) parentElement).getModifierList();
                if (getCoroutineType(modifier) == null)
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");
                break;
            } else if (parent instanceof ALittleClassStaticDec) {
                PsiElement parentElement = parent.getParent();
                if (!(parentElement instanceof ALittleClassElementDec))
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");

                List<ALittleModifier> modifier = ((ALittleClassElementDec) parentElement).getModifierList();
                if (getCoroutineType(modifier) == null)
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");
                break;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                PsiElement parentElement = parent.getParent();
                if (!(parentElement instanceof ALittleNamespaceElementDec))
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");

                List<ALittleModifier> modifier = ((ALittleNamespaceElementDec) parentElement).getModifierList();
                if (getCoroutineType(modifier) == null)
                    throw new ALittleGuessException(element, "所在函数没有async或await修饰");
                break;
            }
            parent = parent.getParent();
        }
    }


    // 判断是否存在
    public static void checkError(PsiElement parent, List<ALittleModifier> element_list) throws ALittleGuessException {
        int register_count = 0;
        int coroutine_count = 0;
        int access_count = 0;

        int language_count = 0;
        int const_count = 0;
        int nullable_count = 0;
        int proto_cmd_count = 0;
        int native_count = 0;

        for (ALittleModifier element : element_list) {
            if (element.getRegisterModifier() != null) {
                ++register_count;
                if (register_count > 1)
                    throw new ALittleGuessException(element.getRegisterModifier(), "register修饰符只能定义一个");

                // register只能修饰namespace
                if (!(parent instanceof ALittleNamespaceDec))
                    throw new ALittleGuessException(element.getRegisterModifier(), "register只能修饰namespace");
            } else if (element.getCoroutineModifier() != null) {
                ++coroutine_count;
                if (coroutine_count > 1)
                    throw new ALittleGuessException(element.getCoroutineModifier(), "协程修饰符只能定义一个");

                boolean has_error = true;
                if (parent instanceof ALittleNamespaceElementDec) {
                    ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec) parent;
                    has_error = namespace_element_dec.getGlobalMethodDec() == null;
                } else if (parent instanceof ALittleClassElementDec) {
                    ALittleClassElementDec class_element_dec = (ALittleClassElementDec) parent;
                    has_error = class_element_dec.getClassMethodDec() == null
                            && class_element_dec.getClassStaticDec() == null;
                }

                if (has_error)
                    throw new ALittleGuessException(element.getCoroutineModifier(), "协程修饰符修饰全局函数，类成员函数，类静态函数");
            } else if (element.getAccessModifier() != null) {
                ++access_count;
                if (access_count > 1)
                    throw new ALittleGuessException(element.getAccessModifier(), "访问修饰符只能定义一个");

                if (parent instanceof ALittleMethodParamOneDec)
                    throw new ALittleGuessException(element.getAccessModifier(), "访问修饰符不能修饰函数形参");

                if (parent instanceof ALittleNamespaceDec)
                    throw new ALittleGuessException(element.getAccessModifier(), "访问修饰符不能修饰namespace");

                if (parent instanceof ALittleAllExpr)
                    throw new ALittleGuessException(element.getAccessModifier(), "访问修饰符不能修饰表达式列表");
            } else if (element.getAttributeModifier() != null) {
                ALittleAttributeModifier attribute = element.getAttributeModifier();
                if (attribute.getLanguageModifier() != null) {
                    ++language_count;
                    if (language_count > 1)
                        throw new ALittleGuessException(attribute.getLanguageModifier(), "Language修饰符最多只能有一个");

                    if (parent instanceof ALittleMethodParamOneDec)
                        throw new ALittleGuessException(attribute, "Language修饰符不能修饰函数形参");
                } else if (attribute.getConstModifier() != null) {
                    ++const_count;
                    if (const_count > 1)
                        throw new ALittleGuessException(attribute.getConstModifier(), "Const修饰符最多只能有一个");

                    boolean has_error = true;
                    if (parent instanceof ALittleClassElementDec) {
                        ALittleClassElementDec class_element_dec = (ALittleClassElementDec) parent;
                        has_error = class_element_dec.getClassGetterDec() == null
                                && class_element_dec.getClassSetterDec() == null
                                && class_element_dec.getClassMethodDec() == null;
                    }

                    if (has_error)
                        throw new ALittleGuessException(attribute, "Const修饰符修饰类getter函数，类setter函数，类成员函数");
                } else if (attribute.getNullableModifier() != null) {
                    ++nullable_count;
                    if (nullable_count > 1)
                        throw new ALittleGuessException(attribute.getNullableModifier(), "Nullable修饰符最多只能有一个");

                    if (!(parent instanceof ALittleMethodParamOneDec))
                        throw new ALittleGuessException(attribute.getNullableModifier(), "Nullable只能修饰函数形参");
                } else if (attribute.getProtocolModifier() != null) {
                    ++proto_cmd_count;
                    if (proto_cmd_count > 1)
                        throw new ALittleGuessException(attribute.getProtocolModifier(), "协议修饰符和命令修饰符最多只能有一个");

                    boolean has_error = true;
                    if (parent instanceof ALittleNamespaceElementDec) {
                        ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec) parent;
                        has_error = namespace_element_dec.getGlobalMethodDec() == null;
                    }

                    if (has_error)
                        throw new ALittleGuessException(attribute, "协议修饰符只能修饰全局函数");
                } else if (attribute.getCommandModifier() != null) {
                    ++proto_cmd_count;
                    if (proto_cmd_count > 1)
                        throw new ALittleGuessException(attribute.getCommandModifier(), "协议修饰符和命令修饰符最多只能有一个");

                    boolean has_error = true;
                    if (parent instanceof ALittleNamespaceElementDec) {
                        ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec) parent;
                        has_error = namespace_element_dec.getGlobalMethodDec() == null;
                    }

                    if (has_error)
                        throw new ALittleGuessException(attribute, "命令修饰符只能修饰全局函数");
                } else if (attribute.getNativeModifier() != null) {
                    ++native_count;
                    if (native_count > 1)
                        throw new ALittleGuessException(attribute.getCommandModifier(), "原生修饰符和命令修饰符最多只能有一个");

                    boolean has_error = true;
                    if (parent instanceof ALittleClassElementDec) {
                        ALittleClassElementDec class_element_dec = (ALittleClassElementDec) parent;
                        if (class_element_dec.getClassVarDec() != null) {
                            ALittleGuess guess = class_element_dec.getClassVarDec().guessType();
                            if (guess instanceof ALittleGuessList)
                                has_error = false;
                        }
                    } else if (parent instanceof ALittleAllExpr) {
                        ALittleAllExpr all_expr = (ALittleAllExpr) parent;
                        if (all_expr.getForExpr() != null)
                            has_error = false;
                    } else if (parent instanceof ALittleNamespaceElementDec) {
                        ALittleNamespaceElementDec namespace_element_dec = (ALittleNamespaceElementDec) parent;
                        has_error = namespace_element_dec.getClassDec() == null;
                    }

                    if (has_error)
                        throw new ALittleGuessException(attribute, "Native修饰符只能修饰class、类成员List的变量、for表达式");
                }
            }
        }
    }

    // 获取是否是register
    public static boolean isRegister(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getRegisterModifier() != null)
                return true;
        }
        return false;
    }

    // 获取是否是Const
    public static boolean isConst(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null
                    && element.getAttributeModifier().getConstModifier() != null)
                return true;
        }
        return false;
    }

    // 获取是否是Nullable
    public static boolean isNullable(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null
                    && element.getAttributeModifier().getNullableModifier() != null)
                return true;
        }
        return false;
    }

    // 获取协程类型
    @NotNull
    public static String getCoroutineType(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getCoroutineModifier() != null)
                return element.getCoroutineModifier().getText();
        }
        return "";
    }

    // 获取协议类型
    public static String getProtocolType(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null
                    && element.getAttributeModifier().getProtocolModifier() != null)
                return element.getAttributeModifier().getProtocolModifier().getText();
        }
        return null;
    }

    public static class CommandInfo {
        public String type;
        public String desc;
    }

    // 获取命令类型
    public static CommandInfo getCommandDetail(List<ALittleModifier> element_list) {
        CommandInfo info = new CommandInfo();
        for (ALittleModifier element : element_list) {
            if (element.getAttributeModifier() != null
                    && element.getAttributeModifier().getCommandModifier() != null) {
                ALittleCommandBodyDec body_dec = element.getAttributeModifier().getCommandModifier().getCommandBodyDec();
                if (body_dec != null && body_dec.getTextContent() != null) {
                    info.desc = body_dec.getTextContent().getText();
                    info.desc = info.desc.substring(1, info.desc.length() - 2);
                }
                info.type = "Cmd";
            }
        }
        return info;
    }

    // 获取访问权限类型
    public static ClassAccessType calcAccessType(List<ALittleModifier> element_list) {
        for (ALittleModifier element : element_list) {
            if (element.getAccessModifier() != null) {
                String text = element.getAccessModifier().getText();
                if (text.equals("public"))
                    return ClassAccessType.PUBLIC;
                else if (text.equals("protected"))
                    return ClassAccessType.PROTECTED;
                return ClassAccessType.PRIVATE;
            }
        }
        return ClassAccessType.PRIVATE;
    }

    // 访问权限等级
    public static int sAccessOnlyPublic = 1;            // 可以访问public的属性和方法
    public static int sAccessProtectedAndPublic = 2;         // 可以访问public protected的属性和方法
    public static int sAccessPrivateAndProtectedAndPublic = 3;           // 可以public protected private的属性和方法

    // 获取某个元素的命名域对象
    public static ALittleNamespaceDec getNamespaceDec(@NotNull PsiFile psiFile) {
        for (PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                return ((ALittleNamespaceDec) child);
            }
        }
        return null;
    }

    // 获取某个元素的命名域对象
    public static ALittleNamespaceNameDec getNamespaceNameDec(@NotNull PsiFile psiFile) {
        ALittleNamespaceDec namespace_dec = getNamespaceDec(psiFile);
        if (namespace_dec == null) return null;
        return namespace_dec.getNamespaceNameDec();
    }

    // 判断某个是不是register
    public static boolean isRegister(PsiElement element) {
        ALittleNamespaceDec namespace_dec = getNamespaceDec(element.getContainingFile());
        if (namespace_dec == null) return false;

        return isRegister(namespace_dec.getModifierList());
    }

    // 获取某个元素的命名域
    @NotNull
    public static String getNamespaceName(@NotNull PsiElement element) {
        return getNamespaceName(element.getContainingFile().getOriginalFile());
    }

    @NotNull
    public static String getNamespaceName(PsiFile psiFile) {
        if (psiFile == null) return "";
        for (PsiElement child = psiFile.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof ALittleNamespaceDec) {
                ALittleNamespaceNameDec nameDec = ((ALittleNamespaceDec) child).getNamespaceNameDec();
                if (nameDec == null) return "";
                return nameDec.getText();
            }
        }
        return "";
    }

    // 获取这个元素所在的类
    public static ALittleClassDec findClassDecFromParent(@NotNull PsiElement dec) {
        while (dec != null && !(dec instanceof PsiFile)) {
            if (dec instanceof ALittleClassDec) {
                return (ALittleClassDec) dec;
            }
            dec = dec.getParent();
        }
        return null;
    }

    // 获取这个元素所在的函数模板
    public static ALittleTemplateDec findMethodTemplateDecFromParent(@NotNull PsiElement dec) {
        while (dec != null && !(dec instanceof PsiFile)) {
            if (dec instanceof ALittleClassDec) {
                return null;
            } else if (dec instanceof ALittleClassCtorDec) {
                return null;
            } else if (dec instanceof ALittleClassGetterDec) {
                return null;
            } else if (dec instanceof ALittleClassSetterDec) {
                return null;
            } else if (dec instanceof ALittleClassStaticDec) {
                return ((ALittleClassStaticDec) dec).getTemplateDec();
            } else if (dec instanceof ALittleClassMethodDec) {
                return ((ALittleClassMethodDec) dec).getTemplateDec();
            } else if (dec instanceof ALittleGlobalMethodDec) {
                return ((ALittleGlobalMethodDec) dec).getTemplateDec();
            }
            dec = dec.getParent();
        }
        return null;
    }

    // 检查是否在静态函数中
    public static boolean isInClassStaticMethod(@NotNull PsiElement dec) {
        PsiElement parent = dec;
        while (parent != null) {
            if (parent instanceof ALittleNamespaceDec) {
                return false;
            } else if (parent instanceof ALittleClassDec) {
                return false;
            } else if (parent instanceof ALittleClassCtorDec) {
                return false;
            } else if (parent instanceof ALittleClassSetterDec) {
                return false;
            } else if (parent instanceof ALittleClassMethodDec) {
                return false;
            } else if (parent instanceof ALittleClassStaticDec) {
                return true;
            } else if (parent instanceof ALittleGlobalMethodDec) {
                return false;
            }
            parent = parent.getParent();
        }

        return false;
    }

    // 根据名称，获取这个结构体的成员列表
    public static void findStructVarDecList(@NotNull ALittleStructDec structDec,
                                            String name,
                                            @NotNull List<ALittleStructVarDec> result,
                                            int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(structDec.getProject());
        if (listener == null) return;

        ALittleStructData data = listener.getStructData(structDec);
        if (data != null) {
            data.findVarDecList(name, result);
        }

        // 处理继承
        ALittleStructDec structExtendsDec = findStructExtends(structDec);
        if (structExtendsDec != null) {
            findStructVarDecList(structExtendsDec, name, result, deep - 1);
        }
    }

    // 根据名称，获取这个枚举中的成员
    public static void findEnumVarDecList(@NotNull ALittleEnumDec enumDec, String name, @NotNull List<ALittleEnumVarDec> result) {
        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(enumDec.getProject());
        if (listener == null) return;

        ALittleEnumData data = listener.getEnumData(enumDec);
        if (data != null) {
            data.findVarDecList(name, result);
        }
    }

    // 计算struct的父类
    public static ALittleStructDec findStructExtends(@NotNull ALittleStructDec dec) {
        ALittleStructExtendsDec structExtendsDec = dec.getStructExtendsDec();
        if (structExtendsDec == null) return null;

        ALittleStructNameDec structNameDec = structExtendsDec.getStructNameDec();
        if (structNameDec == null) return null;

        String namespaceName;
        ALittleNamespaceNameDec structExtendsNamespaceNameDec = structExtendsDec.getNamespaceNameDec();
        if (structExtendsNamespaceNameDec != null) {
            namespaceName = structExtendsNamespaceNameDec.getText();
        } else {
            namespaceName = getNamespaceName(dec);
        }

        PsiElement result = ALittleTreeChangeListener.findALittleNameDec(dec.getProject()
                , PsiHelper.PsiElementType.STRUCT_NAME, dec.getContainingFile().getOriginalFile()
                , namespaceName, structNameDec.getText(), true);
        if (result instanceof ALittleStructNameDec) return (ALittleStructDec) result.getParent();
        return null;
    }

    // 计算class的父类
    public static ALittleClassDec findClassExtends(@NotNull ALittleClassDec dec) {
        ALittleClassExtendsDec classExtendsDec = dec.getClassExtendsDec();
        if (classExtendsDec == null) return null;

        ALittleClassNameDec classNameDec = classExtendsDec.getClassNameDec();
        if (classNameDec == null) return null;

        String namespaceName;
        ALittleNamespaceNameDec classExtendsNamespaceNameDec = classExtendsDec.getNamespaceNameDec();
        if (classExtendsNamespaceNameDec != null) {
            namespaceName = classExtendsNamespaceNameDec.getText();
        } else {
            namespaceName = getNamespaceName(dec);
        }

        PsiElement result = ALittleTreeChangeListener.findALittleNameDec(dec.getProject()
                , PsiHelper.PsiElementType.CLASS_NAME, dec.getContainingFile().getOriginalFile()
                , namespaceName, classNameDec.getText(), true);
        if (result instanceof ALittleClassNameDec) {
            return (ALittleClassDec) result.getParent();
        }
        return null;
    }

    // 过滤名称相同的元素
    public static List<PsiElement> filterSameName(@NotNull List<PsiElement> list) {
        Map<String, PsiElement> map = new HashMap<>();
        for (int i = list.size() - 1; i >= 0; --i) {
            map.put(list.get(i).getText(), list.get(i));
        }
        if (map.size() == list.size()) return list;
        list = new ArrayList<>();
        list.addAll(map.values());
        return list;
    }

    // 计算在dec这个类中，对targetDec成员的访问权限
    public static int calcAccessLevelByTargetClassDec(int accessLevel, @NotNull ALittleClassDec dec, @NotNull ALittleClassDec targetDec) {
        // 如果当前访问权限已经只剩下public，就直接返回
        if (accessLevel <= PsiHelper.sAccessOnlyPublic) {
            return accessLevel;
        }
        // 如果dec和目标dec一致，那么直接返回
        if (dec.equals(targetDec)) {
            return accessLevel;
        }

        // 检查dec的父类，然后判断父类和targetDec的访问权限
        ALittleClassDec classExtendsDec = findClassExtends(dec);
        if (classExtendsDec != null) {
            return calcAccessLevelByTargetClassDec(accessLevel, classExtendsDec, targetDec);
        }

        // 如果没有父类，检查是否是在相同命名域下，如果是那么可以访问public和protected
        if (getNamespaceName(dec).equals(getNamespaceName(targetDec))) {
            return PsiHelper.sAccessProtectedAndPublic;
        }

        // 否则只能访问public
        return PsiHelper.sAccessOnlyPublic;
    }

    // 计算任意元素访问targetDec的访问权限
    public static int calcAccessLevelByTargetClassDecForElement(@NotNull PsiElement element, @NotNull ALittleClassDec targetDec) {
        // 默认为public
        int accessLevel = sAccessOnlyPublic;

        // 如果这个元素在类中，那么可以通过类和targetDec访问权限直接计算
        ALittleClassDec myClassDec = findClassDecFromParent(element);
        if (myClassDec != null) {
            accessLevel = PsiHelper.calcAccessLevelByTargetClassDec(PsiHelper.sAccessPrivateAndProtectedAndPublic, myClassDec, targetDec);
            // 如果元素不在类中，那么element在lua中，或者和targetDec相同，则返回sAccessProtectedAndPublic
        } else {
            String namespaceName = getNamespaceName(element);
            if (element.getContainingFile().getVirtualFile().getPath().equals(targetDec.getContainingFile().getVirtualFile().getPath()))
                accessLevel = sAccessPrivateAndProtectedAndPublic;
            else if (namespaceName.equals("alittle") || namespaceName.equals(PsiHelper.getNamespaceName(targetDec)))
                accessLevel = PsiHelper.sAccessProtectedAndPublic;
        }

        return accessLevel;
    }

    // 根据名称，获取函数列表
    public static void findClassMethodNameDecList(@NotNull ALittleClassDec classDec,
                                                  int accessLevel,
                                                  String name,
                                                  @NotNull List<PsiElement> result,
                                                  int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        ALittleTreeChangeListener listener = ALittleTreeChangeListener.getListener(classDec.getProject());
        if (listener == null) return;

        ALittleClassData data = listener.getClassData(classDec);
        if (data != null) {
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.FUN, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.GETTER, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.SETTER, name, result);
            data.findClassAttrList(accessLevel, PsiHelper.ClassAttrType.STATIC, name, result);
        }

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            findClassMethodNameDecList(classExtendsDec, accessLevel, name, result, deep - 1);
        }
    }

    // 根据名称，获取类的属性列表
    public static void findClassAttrList(@NotNull ALittleClassDec classDec,
                                         int accessLevel,
                                         PsiHelper.ClassAttrType attrType,
                                         String name,
                                         @NotNull List<PsiElement> result,
                                         int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return;

        // 处理成员
        ALittleTreeChangeListener.findClassAttrList(classDec, accessLevel, attrType, name, result);

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            findClassAttrList(classExtendsDec, accessLevel, attrType, name, result, deep - 1);
        }
    }

    // 根据名称，获取继承的构造函数
    public static ALittleClassCtorDec findFirstCtorDecFromExtends(@NotNull ALittleClassDec classDec, int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        // 获取class体
        ALittleClassBodyDec bodyDec = classDec.getClassBodyDec();
        if (bodyDec == null) return null;

        // 处理成员函数
        List<ALittleClassElementDec> classElementDecList = bodyDec.getClassElementDecList();
        for (ALittleClassElementDec elementDec : classElementDecList) {
            if (elementDec.getClassCtorDec() != null)
                return elementDec.getClassCtorDec();
        }

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            return findFirstCtorDecFromExtends(classExtendsDec, deep - 1);
        }

        return null;
    }

    // 根据名称，获取继承的属性
    public static PsiElement findFirstClassAttrFromExtends(@NotNull ALittleClassDec classDec,
                                                           PsiHelper.ClassAttrType attrType,
                                                           String name,
                                                           int deep) {
        // 这个用于跳出无限递归
        if (deep <= 0) return null;

        // 处理setter函数
        PsiElement result = ALittleTreeChangeListener.findClassAttr(classDec,
                PsiHelper.sAccessPrivateAndProtectedAndPublic, attrType, name);
        if (result != null) return result;

        // 处理继承
        ALittleClassDec classExtendsDec = findClassExtends(classDec);
        if (classExtendsDec != null) {
            return findFirstClassAttrFromExtends(classExtendsDec, attrType, name, deep - 1);
        }

        return null;
    }

    // 根据名称，查找函数的参数列表
    @NotNull
    public static List<ALittleMethodParamNameDec> findMethodParamNameDecList(PsiElement methodDec, String name) {
        List<ALittleMethodParamOneDec> paramOneDecList = new ArrayList<>();
        // 处理构造函数的参数列表
        if (methodDec instanceof ALittleClassCtorDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassCtorDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
            // 处理成员函数的参数列表
        } else if (methodDec instanceof ALittleClassMethodDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassMethodDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
            // 处理静态函数的参数列表
        } else if (methodDec instanceof ALittleClassStaticDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleClassStaticDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
            // 处理setter函数的参数列表
        } else if (methodDec instanceof ALittleClassSetterDec) {
            ALittleMethodSetterParamDec methodSetterParamDec = ((ALittleClassSetterDec) methodDec).getMethodSetterParamDec();
            if (methodSetterParamDec != null) {
                ALittleMethodParamOneDec paramOneDec = methodSetterParamDec.getMethodParamOneDec();
                if (paramOneDec != null) {
                    paramOneDecList = new ArrayList<>();
                    paramOneDecList.add(paramOneDec);
                }
            }
            // 处理全局函数的参数列表
        } else if (methodDec instanceof ALittleGlobalMethodDec) {
            ALittleMethodParamDec methodParamDec = ((ALittleGlobalMethodDec) methodDec).getMethodParamDec();
            if (methodParamDec != null) {
                List<ALittleMethodParamOneDec> paramOneDecTmpList = methodParamDec.getMethodParamOneDecList();
                paramOneDecList.addAll(paramOneDecTmpList);
            }
        }

        // 收集所有的参数名元素
        List<ALittleMethodParamNameDec> result = new ArrayList<>();
        for (ALittleMethodParamOneDec oneDec : paramOneDecList) {
            ALittleMethodParamNameDec nameDec = oneDec.getMethodParamNameDec();
            if (nameDec == null) continue;
            if (name.isEmpty() || nameDec.getText().equals(name))
                result.add(nameDec);
        }
        return result;
    }

    // 根据名称，查找变量名所在的定义元素
    @NotNull
    public static List<ALittleVarAssignNameDec> findVarAssignNameDecList(PsiElement element, String name) {
        List<ALittleVarAssignNameDec> var_dec_list = new ArrayList<>();
        // 计算出所在的表达式
        PsiElement parent = element;
        while (parent != null) {
            if (parent instanceof ALittleAllExpr) {
                findVarAssignNameDecList((ALittleAllExpr) parent, var_dec_list, name);
                break;
            }
            if (parent instanceof ALittleForStepCondition) {
                PsiElement parentElement = parent.getParent();
                if (parentElement == null) break;
                if (!(parentElement instanceof ALittleForCondition)) break;
                ALittleForCondition for_condition = (ALittleForCondition) parentElement;
                parentElement = for_condition.getParent();
                if (!(parentElement instanceof ALittleForExpr)) break;
                ALittleForExpr for_expr = (ALittleForExpr) parentElement;
                findVarAssignNameDecList(for_expr, var_dec_list, name);
                break;
            }
            parent = parent.getParent();
        }

        return var_dec_list;
    }

    private static void findVarAssignNameDecList(ALittleForExpr for_expr, List<ALittleVarAssignNameDec> var_dec_list, String name) {
        PsiElement parent = for_expr.getParent();
        if (!(parent instanceof ALittleAllExpr)) return;
        findVarAssignNameDecList((ALittleAllExpr) parent, var_dec_list, name);

        ALittleForCondition for_condition = for_expr.getForCondition();
        if (for_condition != null)
            FindVarAssignNameDecList(for_condition, var_dec_list, name);
    }

    private static void FindVarAssignNameDecList(ALittleForCondition for_condition, List<ALittleVarAssignNameDec> var_dec_list, String name) {
        ALittleForPairDec for_pair_dec = for_condition.getForPairDec();
        if (for_pair_dec != null) {
            // 步进式的for有一个临时变量
            if (for_condition.getForStepCondition() != null) {
                ALittleForStartStat start_stat = for_condition.getForStepCondition().getForStartStat();
                if (start_stat != null) {
                    ALittleVarAssignNameDec var_assign_name_dec = for_pair_dec.getVarAssignNameDec();
                    if (var_assign_name_dec != null) {
                        if (name.length() == 0 || name.equals(var_assign_name_dec.getText()))
                            var_dec_list.add(var_assign_name_dec);
                    }
                }
            }
            // 迭代式的for有多个临时变量
            else if (for_condition.getForInCondition() != null) {
                List<ALittleForPairDec> src_pair_dec_list = for_condition.getForInCondition().getForPairDecList();
                List<ALittleForPairDec> pair_dec_list = new ArrayList<>(src_pair_dec_list);
                pair_dec_list.add(0, for_pair_dec);
                for (ALittleForPairDec pair_dec : pair_dec_list) {
                    ALittleVarAssignNameDec var_assign_name_dec = pair_dec.getVarAssignNameDec();
                    if (var_assign_name_dec != null) {
                        if (name.length() == 0 || name.equals(var_assign_name_dec.getText()))
                            var_dec_list.add(var_assign_name_dec);
                    }
                }
            }
        }
    }

    // 根据名称，查找定义表达式名列表
    private static void findVarAssignNameDecList(@NotNull ALittleAllExpr allExpr,
                                                 @NotNull List<ALittleVarAssignNameDec> varDecList,
                                                 @NotNull String name) {
        PsiElement parent = allExpr.getParent();
        List<ALittleAllExpr> allExprList = null;

        // 处理函数体
        if (parent instanceof ALittleMethodBodyDec) {
            ALittleMethodBodyDec curExpr = (ALittleMethodBodyDec) parent;
            allExprList = curExpr.getAllExprList();
            // 处理for循环
        } else if (parent instanceof ALittleForExpr || parent instanceof ALittleForBody) {
            if (parent instanceof ALittleForBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent(), varDecList, name);

            ALittleForExpr curExpr = (ALittleForExpr) parent;
            // 获取for内部的表达式
            if (curExpr.getForBody() != null)
                allExprList = curExpr.getForBody().getAllExprList();
            if (curExpr.getAllExpr() != null) {
                allExprList = new ArrayList<>();
                allExprList.add(curExpr.getAllExpr());
            }

            ALittleForCondition for_condition = curExpr.getForCondition();
            if (for_condition != null)
                FindVarAssignNameDecList(for_condition, varDecList, name);
            // 处理while循环
        } else if (parent instanceof ALittleWhileExpr || parent instanceof ALittleWhileBody) {
            if (parent instanceof ALittleWhileBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent(), varDecList, name);
            ALittleWhileExpr curExpr = (ALittleWhileExpr) parent;
            if (curExpr.getWhileBody() != null)
                allExprList = curExpr.getWhileBody().getAllExprList();
            else if (curExpr.getAllExpr() != null) {
                allExprList = new ArrayList<>();
                allExprList.add(curExpr.getAllExpr());
            }
            // 处理do while
        } else if (parent instanceof ALittleDoWhileExpr || parent instanceof ALittleDoWhileBody) {
            if (parent instanceof ALittleDoWhileBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent(), varDecList, name);
            ALittleDoWhileExpr curExpr = (ALittleDoWhileExpr) parent;
            if (curExpr.getDoWhileBody() != null)
                allExprList = curExpr.getDoWhileBody().getAllExprList();
            // 处理 if
        } else if (parent instanceof ALittleIfExpr || parent instanceof ALittleIfBody) {
            if (parent instanceof ALittleIfBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent(), varDecList, name);
            ALittleIfExpr curExpr = (ALittleIfExpr) parent;
            if (curExpr.getIfBody() != null)
                allExprList = curExpr.getIfBody().getAllExprList();
            else if (curExpr.getAllExpr() != null) {
                allExprList = new ArrayList<>();
                allExprList.add(curExpr.getAllExpr());
            }
            // 处理 else if
        } else if (parent instanceof ALittleElseIfExpr || parent instanceof ALittleElseIfBody) {
            if (parent instanceof ALittleElseIfBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent().getParent(), varDecList, name);
            ALittleElseIfExpr curExpr = (ALittleElseIfExpr) parent;
            if (curExpr.getElseIfBody() != null)
                allExprList = curExpr.getElseIfBody().getAllExprList();
            else if (curExpr.getAllExpr() != null) {
                allExprList = new ArrayList<>();
                allExprList.add(curExpr.getAllExpr());
            }
            // 处理 else
        } else if (parent instanceof ALittleElseExpr || parent instanceof ALittleElseBody) {
            if (parent instanceof ALittleElseBody) parent = parent.getParent();
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent().getParent(), varDecList, name);
            ALittleElseExpr curExpr = (ALittleElseExpr) parent;
            if (curExpr.getElseBody() != null)
                allExprList = curExpr.getElseBody().getAllExprList();
            else if (curExpr.getAllExpr() != null) {
                allExprList = new ArrayList<>();
                allExprList.add(curExpr.getAllExpr());
            }
            // 处理 wrap
        } else if (parent instanceof ALittleWrapExpr) {
            findVarAssignNameDecList((ALittleAllExpr) parent.getParent(), varDecList, name);
            ALittleWrapExpr curExpr = (ALittleWrapExpr) parent;
            allExprList = curExpr.getAllExprList();
        }

        if (allExprList == null) return;

        for (ALittleAllExpr expr : allExprList) {
            // 如果已经遍历到当前，那么就可以返回了
            if (expr.equals(allExpr)) return;

            // 获取定义表达式
            ALittleVarAssignExpr varAssignExpr = expr.getVarAssignExpr();
            if (varAssignExpr == null) continue;

            // 获取变量名列表
            List<ALittleVarAssignDec> varAssignDecList = varAssignExpr.getVarAssignDecList();
            for (ALittleVarAssignDec varAssignDec : varAssignDecList) {
                ALittleVarAssignNameDec varAssignNameDec = varAssignDec.getVarAssignNameDec();
                if (varAssignNameDec == null) continue;
                String varName = varAssignNameDec.getText();
                if (name.isEmpty() || name.equals(varName))
                    varDecList.add(varAssignNameDec);
            }
        }
    }

    // 检查迭代函数
    public static boolean isPairsFunction(List<ALittleGuess> guess_list) {
        // guess_list长度必须是3
        if (guess_list.size() != 3) return false;
        // 第一个必须是函数
        if (!(guess_list.get(0) instanceof ALittleGuessFunctor)) return false;
        ALittleGuessFunctor guess = (ALittleGuessFunctor) guess_list.get(0);
        // 函数不能带await
        if (guess.await_modifier) return false;
        // 函数不能带proto
        if (guess.proto != null) return false;
        // 函数不能是模板函数
        if (guess.template_param_list.size() > 0) return false;
        // 函数参数必须是2个
        if (guess.param_list.size() != 2) return false;
        if (guess.param_nullable_list.size() != 2) return false;
        // 函数的参数不能带Nullable
        if (guess.param_nullable_list.get(0)) return false;
        if (guess.param_nullable_list.get(1)) return false;
        // 函数不能有参数占位符
        if (guess.param_tail != null) return false;
        // 函数必须有返回值，可以是任意个，这个也表示for的变量列表的数量
        if (guess.return_list.size() > 0) return false;
        // 函数不能有返回值占位符
        if (guess.return_tail != null) return false;
        // 函数的第一个参数必须和guess_list第二个参数一致
        if (!guess.param_list.get(0).getValue().equals(guess_list.get(1).getValue())) return false;
        // 函数的第二个参数必须和guess_list第二个参数一致
        if (!guess.param_list.get(1).getValue().equals(guess_list.get(2).getValue())) return false;
        return true;
    }

    // 计算表达式需要使用什么样的变量方式
    public static String calcPairsTypeForLua(ALittleValueStat value_stat) throws ALittleGuessException {
        List<ALittleGuess> guess_list = value_stat.guessTypes();

        // 必出是模板容器
        if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessList) {
            return "___ipairs";
        } else if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessMap) {
            return "___pairs";
        }

        // 已经是迭代函数了，就不需要包围修饰
        if (isPairsFunction(guess_list)) return "";

        throw new ALittleGuessException(value_stat, "该表达式不能遍历");
    }

    // 计算表达式在for中使用in还是of
    public static Tuple2<String, Boolean> calcPairsTypeForJavaScript(ALittleValueStat value_stat) throws ALittleGuessException {
        String result = "Other";
        boolean is_native = false;
        List<ALittleGuess> guess_list = value_stat.guessTypes();
        // 必出是模板容器
        if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessList) {
            result = "List";
            is_native = ((ALittleGuessList) guess_list.get(0)).is_native;
            return new Tuple2<>(result, is_native);
        } else if (guess_list.size() == 1 && guess_list.get(0) instanceof ALittleGuessMap) {
            if (((ALittleGuessMap) guess_list.get(0)).key_type instanceof ALittleGuessString)
                result = "Object";
            else
                result = "Map";
            return new Tuple2<>(result, is_native);
        }

        // 已经是迭代函数了，就不需要包围修饰
        if (isPairsFunction(guess_list)) return new Tuple2<>(result, is_native);

        throw new ALittleGuessException(value_stat, "该表达式不能遍历");
    }

    // 判断 parent是否是child的父类
    public static boolean isClassSuper(ALittleClassDec child, String parent) throws ALittleGuessException {
        // 获取继承
        ALittleClassExtendsDec extends_dec = child.getClassExtendsDec();
        if (extends_dec == null) return false;

        // 获取类名
        ALittleClassNameDec name_dec = extends_dec.getClassNameDec();
        if (name_dec == null) return false;

        // 获取类型
        ALittleGuess guess = name_dec.guessType();

        // 继续判断父类的父类
        ALittleGuessClass guess_class = (ALittleGuessClass) guess;

        // 检查是否一致
        if (guess_class.getValueWithoutConst().equals(parent))
            return true;

        return isClassSuper(guess_class.class_dec, parent);
    }

    // 判断 parent是否是child的父类
    public static boolean isStructSuper(PsiElement child, String parent) throws ALittleGuessException {
        if (!(child instanceof ALittleStructDec)) return false;
        ALittleStructDec struct_child = (ALittleStructDec) child;

        // 获取继承
        ALittleStructExtendsDec extends_dec = struct_child.getStructExtendsDec();
        if (extends_dec == null) return false;

        // 获取结构体名
        ALittleStructNameDec name_dec = extends_dec.getStructNameDec();
        if (name_dec == null) return false;

        // 获取类型
        ALittleGuess guess = name_dec.guessType();

        // 继续判断父结构体的父结构体
        ALittleGuessStruct guess_struct = (ALittleGuessStruct) guess;

        // 判断是否一致
        if (guess_struct.getValueWithoutConst().equals(parent))
            return true;

        return isStructSuper(guess_struct.struct_dec, parent);
    }

    // 判断ValueStat
    public static Tuple2<Integer, List<ALittleGuess>> calcReturnCount(ALittleValueStat value_stat) throws ALittleGuessException {
        int count = 0;
        // 获取右边表达式的
        List<ALittleGuess> guess_list = value_stat.guessTypes();
        count = guess_list.size();
        if (guess_list.size() > 0 && guess_list.get(guess_list.size() - 1) instanceof ALittleGuessReturnTail)
            count = -1;
        return new Tuple2<>(count, guess_list);
    }
}
