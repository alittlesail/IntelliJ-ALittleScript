package plugin.component;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import plugin.alittle.SendLogRunnable;
import plugin.generate.ALittleTranslation;
import plugin.generate.ALittleTranslationJavaScript;
import plugin.generate.ALittleTranslationLua;
import plugin.guess.ALittleGuessException;
import plugin.index.ALittleTreeChangeListener;
import plugin.module.ALittleConfig;
import plugin.psi.ALittleFile;

import java.util.List;

public class ALittleInitComponent implements BaseComponent {
    private boolean USED = false;

    public ALittleInitComponent() {
    }
    @NotNull
    public String getComponentName() {
        return "ALittleInit";
    }

    public void initComponent() {
        // 监听文件变化
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC
                , new FileDocumentManagerListener() {
                    // 某个文件加载完毕，那么就开始启动项目加载
                    @Override
                    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            if (ALittleTreeChangeListener.isReloading(project)) continue;
                            ALittleTreeChangeListener.handleRefresh(project);
                        }
                    }
                }
        );

        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    // 处理删除事件
                    if (event instanceof VFileDeleteEvent) {
                        VirtualFile file = event.getFile();
                        if (file == null) continue;
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            ALittleTreeChangeListener.handleDirDelete(project, file);
                        }
                    }
                }
            }

            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    // 处理创建事件
                    if (event instanceof VFileCreateEvent) {
                        VirtualFile virtualFile = event.getFile();
                        if (virtualFile == null) continue;
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            ALittleTreeChangeListener.handleDirCreated(project, virtualFile);
                        }
                    // 处理保存
                    } else if (event instanceof VFileContentChangeEvent) {
                        VirtualFile virtualFile = event.getFile();
                        if (virtualFile == null) continue;
                        Project[] myProject = ProjectManager.getInstance().getOpenProjects();
                        for (Project project : myProject) {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                            if (!(psiFile instanceof ALittleFile)) {
                                continue;
                            }
                            if (!USED) {
                                SendLogRunnable.SendLog("fist use gen a single lua file");
                                USED = true;
                            }
                            ALittleTranslation translation = null;
                            String language = ALittleConfig.getConfig(project).getTargetLanguage();
                            if (language.equals("Lua"))
                                translation = new ALittleTranslationLua();
                            else if (language.equals("JavaScript"))
                                translation = new ALittleTranslationJavaScript();
                            try {
                                if (translation != null)
                                    translation.generate((ALittleFile) psiFile,false);
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
            }
        });

        // 监听工程的打开和关闭
        ProjectManager.getInstance().addProjectManagerListener(
                new VetoableProjectManagerListener() {
                    // 工程打开后，添加到集合中
                    @Override
                    public void projectOpened(@NotNull Project project) {
                        ALittleTreeChangeListener listener = new ALittleTreeChangeListener(project);
                        ALittleTreeChangeListener.sMap.put(project, listener);
                    }

                    // 当工程关闭后，从集合中删除
                    @Override
                    public void projectClosed(@NotNull Project project) {
                        ALittleTreeChangeListener listener = ALittleTreeChangeListener.sMap.get(project);
                        if (listener == null) return;
                        PsiManager.getInstance(project).removePsiTreeChangeListener(listener);
                        ALittleTreeChangeListener.sMap.remove(project);
                    }

                    // 返回true，都可以关闭
                    @Override
                    public boolean canClose(@NotNull Project var1) {
                        return true;
                    }
                }
        );

        SendLogRunnable.SendLog("initComponent");
    }

    public void disposeComponent() {
        SendLogRunnable.SendLog("disposeComponent");
    }
}
