package plugin;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.scope.packageSet.NamedScopeManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import plugin.psi.ALittleFile;

public class ALittleInitComponent implements ApplicationComponent {
    public ALittleInitComponent() {
    }
    @NotNull
    public String getComponentName() {
        return "ALittleInit";
    }

    public void initComponent() {
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {
            @Override
            public void beforeDocumentSaving(@NotNull Document document) {
                Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                for (Project project : myProject) {
                    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document); // THIS IS ALWAYS NULL
                    if (psiFile instanceof ALittleFile) {
                        ALittleGenerateLua lua = new ALittleGenerateLua();
                        String error = lua.GenerateLua((ALittleFile) psiFile, false);
                        if (error == null) {
                            System.out.println(psiFile.getName() + ":代码生成成功");
                        } else {
                            System.out.println(psiFile.getName() + ":代码生成失败:" + error);
                        }
                        break;
                    }
                }
            }
        });

        ProjectManager.getInstance().addProjectManagerListener(
                new ProjectManagerListener() {
                    @Override
                    public void projectOpened(@NotNull Project project) {
                        ALittleTreeChangeListener listener = new ALittleTreeChangeListener(project);
                        ALittleTreeChangeListener.s_map.put(project, listener);
                        listener.reload();
                        PsiManager.getInstance(project).addPsiTreeChangeListener(listener);
                    }

                    @Override
                    public void projectClosed(@NotNull Project project) {
                        ALittleTreeChangeListener listener = ALittleTreeChangeListener.s_map.get(project);
                        if (listener == null) return;
                        PsiManager.getInstance(project).removePsiTreeChangeListener(listener);
                        ALittleTreeChangeListener.s_map.remove(project);
                    }
                }
        );

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener()
        {
            public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
                Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                for (Project project : myProject) {
                    PsiFile file = PsiManager.getInstance(project).findFile(event.getFile());
                    if (!(file instanceof ALittleFile)) continue;

                    ALittleTreeChangeListener.handleFileDelete(project, (ALittleFile)file);
                }
            }
        });
    }

    public void disposeComponent() {

    }
}
