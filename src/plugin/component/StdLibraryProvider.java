package plugin.component;

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

public class StdLibraryProvider extends AdditionalLibraryRootsProvider {
    public static boolean isPluginSelf(@NotNull Project project) {
        return project.getName().equals("ALittleScript");
    }

    @Override
    @NotNull
    public Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project) {
        ArrayList<SyntheticLibrary> result = new ArrayList<>();

        // 如果是插件自己，那么就直接返回
        if (isPluginSelf(project)) return result;

        // 获取jar路径
        String jarPath = PathUtil.getJarPathForClass(StdLibraryProvider.class);

        try {
            VirtualFile dir;

            // 获取文件路径
            if (jarPath.endsWith(".jar"))
                dir = VfsUtil.findFileByURL(URLUtil.getJarEntryURL(new File(jarPath), "std"));
            else
                dir = VfsUtil.findFileByIoFile(new File(jarPath + "/std"), true);

            // 遍历std目录下所有库，然后返回
            if (dir != null) {
                for (VirtualFile child_dir : dir.getChildren()) {
                    result.add(new StdLibrary(child_dir));
                }
            }
        } catch (MalformedURLException ignored) {

        }

        return result;
    }

    static class StdLibrary extends SyntheticLibrary implements ItemPresentation {
        // 标准库，跟路径
        private VirtualFile m_root;

        public StdLibrary(VirtualFile root) {
            m_root = root;
        }

        @Override
        public int hashCode() {
            return m_root.hashCode();
        }

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
            return Arrays.asList(m_root.getChildren());
        }

        @Override
        public String getLocationString() {
            return "";
        }

        @Override
        public Icon getIcon(boolean var) {
            return ALittleIcons.FILE;
        }

        @Override
        public String getPresentableText() {
            return m_root.getName();
        }
    }
}
