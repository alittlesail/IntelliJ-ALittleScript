package plugin.component;

import com.intellij.lexer.FlexAdapter;
import plugin._ALittleLexer;

import java.io.Reader;

public class ALittleLexerAdapter extends FlexAdapter {
    public ALittleLexerAdapter() {
        super(new _ALittleLexer((Reader) null));
    }
}