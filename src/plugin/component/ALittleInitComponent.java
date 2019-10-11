package plugin.component;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.SendLogRunnable;
import plugin.generate.ALittleGenerateLua;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleIndex;
import plugin.index.ALittleTreeChangeListener;
import plugin.psi.ALittleFile;
import plugin.reference.ALittleReferenceUtil;

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
                    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {
                        // 如果文件是项目内部的，那么就添加进去
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            if (ALittleTreeChangeListener.isReloading(project)) continue;
                            ALittleTreeChangeListener.handleRefresh(project);
                        }
                    }

                    @Override
                    public void beforeDocumentSaving(@NotNull Document document) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                            if (psiFile instanceof ALittleFile) {
                                if (!USED) {
                                    SendLogRunnable.SendLog("fist use gen a single lua file");
                                    USED = true;
                                }

                                ALittleGenerateLua lua = new ALittleGenerateLua();
                                try {
                                    lua.GenerateLua((ALittleFile) psiFile, false);
                                } catch (ALittleGuessException e) {
                                    System.out.println(psiFile.getName() + ":生成lua代码失败:" + e.getError());
                                } catch (Exception e) {
                                    System.out.println(psiFile.getName() + ":生成lua代码失败:" + e.getMessage());
                                    break;
                                }
                                System.out.println(psiFile.getName() + ":生成lua代码成功");
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
                        ALittleTreeChangeListener.sMap.put(project, listener);
                    }

                    @Override
                    public void projectClosed(@NotNull Project project) {
                        ALittleTreeChangeListener listener = ALittleTreeChangeListener.sMap.get(project);
                        if (listener == null) return;
                        PsiManager.getInstance(project).removePsiTreeChangeListener(listener);
                        ALittleTreeChangeListener.sMap.remove(project);
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
                            ALittleTreeChangeListener.handleDirDelete(project, event.getFile());
                        }
                    }

                    @Override
                    public void fileCreated(@NotNull VirtualFileEvent event) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            ALittleTreeChangeListener.handleDirCreated(project, event.getFile());
                        }
                    }
        });

        SendLogRunnable.SendLog("initComponent");
    }

    public void disposeComponent() {

        SendLogRunnable.SendLog("disposeComponent");
    }
}
