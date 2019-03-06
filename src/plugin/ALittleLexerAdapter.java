package plugin;


import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

import java.io.Reader;

public class ALittleLexerAdapter extends FlexAdapter {
    public ALittleLexerAdapter() {
        super(new _ALittleLexer((Reader) null));
    }
}