package plugin;


import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class ALittleLexerAdapter extends FlexAdapter {
    public ALittleLexerAdapter() {
        super(new _ALittleLexer((Reader) null));
    }
}