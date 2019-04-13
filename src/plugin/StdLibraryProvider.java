package plugin;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StdLibraryProvider extends AdditionalLibraryRootsProvider {
    @Override
    @NotNull
    public Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project) {
        ArrayList<SyntheticLibrary> result = new ArrayList<>();

        String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);
        VirtualFile dir = null;

        try {
            if (jarPath.endsWith(".jar"))
                dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "std"));
            else
                dir = VfsUtil.findFileByIoFile(new File(jarPath + "/std"), true);

            if (dir != null) {
                for (VirtualFile child_dir : dir.getChildren()) {
                    result.add(new StdLibrary(child_dir));
                }
            }
        } catch (MalformedURLException e) {

        }

        return result;
    }

    class StdLibrary extends SyntheticLibrary implements ItemPresentation
    {
        private VirtualFile m_root;
        public StdLibrary(VirtualFile root) {
            m_root = root;
        }

        @Override
        public int hashCode() { return m_root.hashCode(); }

        @Override
        public boolean equals(Object target) {
            if (!(target instanceof StdLibrary))
                return false;
            StdLibrary lib = (StdLibrary)target;
            return lib.m_root.equals(m_root);
        }

        @Override
        @NotNull
        public Collection<VirtualFile> getSourceRoots() {
            ArrayList<VirtualFile> roots = new ArrayList<>();
            roots.addAll(Arrays.asList(m_root.getChildren()));
            return roots;
        }

        @Override
        public String getLocationString() { return ""; }

        @Override
        public Icon getIcon(boolean var1) { return ALittleIcons.FILE; }

        @Override
        public String getPresentableText() { return m_root.getName(); }
    }
}
