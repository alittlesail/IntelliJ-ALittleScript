package plugin;

import com.intellij.AppTopics;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.SendLogRunnable;
import plugin.psi.ALittleFile;

public class ALittleInitComponent implements BaseComponent {
    private boolean USED = false;

    public ALittleInitComponent() {
    }
    @NotNull
    public String getComponentName() {
        return "ALittleInit";
    }

    public void initComponent() {
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC
                , new FileDocumentManagerListener() {
                    @Override
                    public void beforeDocumentSaving(@NotNull Document document) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                            if (psiFile instanceof ALittleFile) {
                                ALittleGenerateLua lua = new ALittleGenerateLua();
                                String error = lua.GenerateLua((ALittleFile) psiFile, false);
                                if (error == null) {
                                    System.out.println(psiFile.getName() + ":生成lua代码成功");
                                } else {
                                    System.out.println(psiFile.getName() + ":生成lua代码失败:" + error);
                                }

                                if (!USED) {
                                    SendLogRunnable.SendLog("fist use gen a single lua file");
                                    USED = true;
                                }

                                // ALittleGenerateJavaScript js = new ALittleGenerateJavaScript();
                                // error = js.GenerateJavaScript((ALittleFile) psiFile, false);
                                // if (error == null) {
                                //     System.out.println(psiFile.getName() + ":生成javascript代码成功");
                                // } else {
                                //     System.out.println(psiFile.getName() + ":生成javascript代码失败:" + error);
                                // }
                                break;
                            }
                        }
                    }
        });

        ProjectManager.getInstance().addProjectManagerListener(
                new VetoableProjectManagerListener() {
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

                    @Override
                    public boolean canClose(@NotNull Project var1) {
                        return true;
                    }
                }
        );

        VirtualFileManager.getInstance().addVirtualFileListener(
                new VirtualFileListener() {
                    @Override
                    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            // 如果是文件夹
                            if (event.getFile().isDirectory())
                            {
                                VirtualFile[] file_list = event.getFile().getChildren();
                                if (file_list != null) {
                                    for (VirtualFile file : file_list) {
                                        PsiFile psi_file = PsiManager.getInstance(project).findFile(file);
                                        if (!(psi_file instanceof ALittleFile)) continue;

                                        ALittleTreeChangeListener.handleFileDelete(project, (ALittleFile)psi_file);
                                    }
                                }
                            // 如果是文件
                            } else {
                                PsiFile file = PsiManager.getInstance(project).findFile(event.getFile());
                                if (!(file instanceof ALittleFile)) continue;

                                ALittleTreeChangeListener.handleFileDelete(project, (ALittleFile)file);
                            }
                        }
                    }

                    @Override
                    public void fileCreated(@NotNull VirtualFileEvent event) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            // 如果是文件夹
                            if (event.getFile().isDirectory())
                            {
                                VirtualFile[] file_list = event.getFile().getChildren();
                                if (file_list != null) {
                                    for (VirtualFile file : file_list) {
                                        PsiFile psi_file = PsiManager.getInstance(project).findFile(file);
                                        if (!(psi_file instanceof ALittleFile)) continue;

                                        ALittleTreeChangeListener.handleFileCreated(project, (ALittleFile)psi_file);
                                    }
                                }
                                // 如果是文件
                            } else {
                                PsiFile file = PsiManager.getInstance(project).findFile(event.getFile());
                                if (!(file instanceof ALittleFile)) continue;

                                ALittleTreeChangeListener.handleFileCreated(project, (ALittleFile)file);
                            }
                        }
                    }
        });

        SendLogRunnable.SendLog("initComponent");
    }

    public void disposeComponent() {

        SendLogRunnable.SendLog("disposeComponent");
    }
}
