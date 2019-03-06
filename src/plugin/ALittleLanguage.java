package plugin;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class ALittleLanguage extends Language {
    public static final ALittleLanguage INSTANCE = new ALittleLanguage();

    private ALittleLanguage() {
        super("ALittle");
    }

}
