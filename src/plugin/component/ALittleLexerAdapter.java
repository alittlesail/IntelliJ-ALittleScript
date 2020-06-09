package plugin.component;

import com.intellij.lexer.FlexAdapter;
import plugin._ALittleLexer;

public class ALittleLexerAdapter extends FlexAdapter {
    public ALittleLexerAdapter() {
        super(new _ALittleLexer(null));
    }
}