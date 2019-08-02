package plugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import plugin.psi.*;

public class ALittleCompletionContributor extends CompletionContributor {

    static String[] KEYWORD_LIST = new String[] {
            "int","I64","double","bool","string","auto","List","Map"
            ,"namespace","class","struct","enum"
            ,"for","do","while","break","return","cast","reflect"
            ,"get","set","fun","static","Ctor","Functor","this"
            ,"public","protected","private","instance"
            ,"async","await","yield"
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
                        for (String keyword : KEYWORD_LIST) {
                            resultSet.addElement(LookupElementBuilder.create(keyword));
                        }
                    }
                }
        );
    }
}

