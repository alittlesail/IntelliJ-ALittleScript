package plugin.reference;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleNamespaceDec;
import plugin.psi.ALittleNamespaceNameDec;

import java.util.ArrayList;
import java.util.List;

public class ALittleNamespaceDecReference extends ALittleReference<ALittleNamespaceDec> {
    public ALittleNamespaceDecReference(@NotNull ALittleNamespaceDec element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    public List<ALittleReferenceUtil.GuessTypeInfo> guessTypes() throws ALittleReferenceUtil.ALittleReferenceException {
        ALittleNamespaceNameDec namespaceNameDec = myElement.getNamespaceNameDec();
        if (namespaceNameDec == null) {
            throw new ALittleReferenceUtil.ALittleReferenceException(myElement, "没有定义命名域");
        }

        ALittleReferenceUtil.GuessTypeInfo info = new ALittleReferenceUtil.GuessTypeInfo();
        info.type = ALittleReferenceUtil.GuessType.GT_NAMESPACE;
        info.value = namespaceNameDec.getIdContent().getText();
        info.element = myElement;

        List<ALittleReferenceUtil.GuessTypeInfo> guessList = new ArrayList<>();
        guessList.add(info);
        return guessList;
    }
}
