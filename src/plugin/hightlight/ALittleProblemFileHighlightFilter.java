package plugin.hightlight;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import plugin.filetype.ALittleFileType;

public class ALittleProblemFileHighlightFilter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile file) {
        return file.getFileType().equals(ALittleFileType.INSTANCE);
    }
}
