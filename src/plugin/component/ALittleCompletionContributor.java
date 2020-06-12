package plugin.component;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittlePropertyValueDotIdName;
import plugin.psi.ALittleTypes;

public class ALittleCompletionContributor extends CompletionContributor {

    static String[] KEYWORD_LIST = new String[]{
            "int", "long", "double", "bool", "string", "var", "List", "Map"
            , "namespace", "class", "struct", "enum", "const"
            , "for", "do", "while", "break", "return", "cast", "reflect", "continue", "paths"
            , "get", "set", "fun", "static", "Ctor", "Functor", "this", "using"
            , "public", "protected", "private", "instance", "tcall", "Language"
            , "async", "await", "yield", "bind", "Http", "HttpDownload", "HttpUpload", "Msg", "Cmd", "Constant"
    };

    public ALittleCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(ALittleTypes.ID_CONTENT).withLanguage(ALittleLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement element = parameters.getPosition();
                        PsiElement parent = element.getParent();
                        if (parent instanceof ALittlePropertyValueDotIdName) {
                            return;
                        }
                        String text = element.getText();
                        if (text.equals("IntellijIdeaRulezzz")) {
                            return;
                        }
                        for (String keyword : KEYWORD_LIST) {
                            resultSet.addElement(LookupElementBuilder.create(keyword));
                        }
                    }
                }
        );
    }
}

