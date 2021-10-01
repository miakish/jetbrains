package ru.poidem.intellij.plugins.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import ru.poidem.intellij.plugins.ui.PoidemSettings;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.poidem.intellij.plugins.util.Util.lastChoosedFile;

public abstract class AbstractAction extends AnAction {

    protected String actionText = StringUtils.EMPTY;




    public <T> void myActionPerformed(AnActionEvent anActionEvent, Class<T> clazz) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }
        final PoidemSettings poidemSettings = project.getService(PoidemSettings.class);
        final JPAMappingSettings jpaMappingSettings = project.getService(JPAMappingSettings.class);
        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }
        for (PsiElement psiElement : psiElements) {

//            if (!(psiElement.getClass().isAssignableFrom(clazz))) {
//                continue;
//            }

            fileChoose(project);

            Map<String, String> additionalProperties = new HashMap<>();
            final GitRepositoryManager manager = GitUtil.getRepositoryManager(project);
            GitRepository repository = manager.getRepositoryForRootQuick(ProjectUtil.guessProjectDir(project));
            String gitBranch;
            if(repository!=null) {
                gitBranch = repository.getCurrentBranch().getName();
            } else {
                gitBranch = "";
            }
            additionalProperties.put("GIT_BRANCH", gitBranch);
            fillAdditionalProperties(psiElement, additionalProperties, jpaMappingSettings, poidemSettings);

            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(lastChoosedFile);
            List<PsiClass> psiClassList = loadTemplates(psiElement, psiDirectory, additionalProperties, jpaMappingSettings);
            for (PsiClass pc: psiClassList) {
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openFile(pc.getContainingFile().getVirtualFile(), true, true);
            }
        }
    }

    public abstract void fillAdditionalProperties(PsiElement psiElement, Map<String, String> additionalProperties, JPAMappingSettings jpaMappingSettings, PoidemSettings poidemSettings);

    public abstract List<PsiClass> loadTemplates(PsiElement psiElement, PsiDirectory psiDirectory, Map<String, String> additionalProperties, JPAMappingSettings jpaMappingSettings);

    public void fileChoose(Project project){
        VirtualFile chooseFile = ProjectUtil.guessProjectDir(project);
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        if (null != lastChoosedFile) {
            chooseFile = lastChoosedFile;
        }
        lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
        if (null == lastChoosedFile) {
            return;
        }
    }

    public void updateAction(@NotNull AnActionEvent anActionEvent, @NotNull Icon img) {
        if (actionText.isEmpty()) {
            actionText = anActionEvent.getPresentation().getText();
        }
        anActionEvent.getPresentation().setIcon(img);
    }

}
