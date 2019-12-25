package plugin.component;

import com.intellij.lang.Language;

public class ALittleLanguage extends Language {
    public static final ALittleLanguage INSTANCE = new ALittleLanguage();

    private ALittleLanguage() {
        super("ALittle");
    }
}
