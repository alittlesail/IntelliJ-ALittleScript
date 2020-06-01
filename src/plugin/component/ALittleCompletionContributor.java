package plugin.component;

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
            ,"get","set","fun","static","Ctor","Functor","this","using"
            ,"public","protected","private","instance","pcall","ncall","nsend"
            ,"async","await","yield","bind","Http","HttpDownload","HttpUpload","Msg","Cmd","Csv","Mysql"
    };

    public ALittleCompletionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(ALittleTypes.ID).withLanguage(ALittleLanguage.INSTANCE),
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

