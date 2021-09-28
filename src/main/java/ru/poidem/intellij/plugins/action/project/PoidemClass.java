package ru.poidem.intellij.plugins.action.project;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class PoidemClass extends AnAction {

    private String actionText = StringUtils.EMPTY;

    public static final String TEMPLATE_NAME = "Class.java";

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        PsiDirectory directory = null;

        for (PsiElement psiElement : psiElements) {
            Messages.showDialog(String.format("%s", psiElement.getClass().getName()), "???", new String[]{"OK"}, -1, null);
            if(psiElement instanceof PsiDirectory) {
                directory = (PsiDirectory) psiElement;
            } else if(psiElement.getParent() instanceof PsiDirectory){
                directory = (PsiDirectory) psiElement.getParent();
            }

        }
        String txt= Messages.showInputDialog(project, "What is your name?",
                "Input your name", Messages.getQuestionIcon());


        if (directory == null) {
            Messages.showDialog(String.format("File %s.", "null"), "???", new String[]{"OK"}, -1, null);
            return;
        }

        try {
            final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(project);
            Properties properties = new Properties(fileTemplateManager.getDefaultProperties());
            final FileTemplate template = fileTemplateManager.getJ2eeTemplate(TEMPLATE_NAME);

            final PsiElement fromTemplate = FileTemplateUtil.createFromTemplate(template, "test", properties, directory);

            openFileInEditor(project, fromTemplate);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    private void openFileInEditor(Project project, PsiElement fromTemplate) {
        final PsiFile createdFile = fromTemplate.getContainingFile();

        if (createdFile != null) {
            final VirtualFile virtualFile = createdFile.getVirtualFile();

            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {

        if (e.getProject() == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        List<Module> modules = getModules(e.getDataContext(), e.getProject());
        final boolean isAtLeastOneModule = !modules.isEmpty();

        e.getPresentation().setEnabled(isAtLeastOneModule);
//        e.getPresentation().setEnabledAndVisible(isAtLeastOneModule && e.getProject() != null);
    }

    private List<Module> getModules(DataContext dataContext, Project project) {
        List<Module> modules = new ArrayList<>();
        if (project != null && dataContext!=null) {

            VirtualFile[] files = LangDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
            if (files!=null) {
                final List<VirtualFile> filesList = Arrays.asList(files);

                modules = filesList.stream().map(file -> ModuleUtil.findModuleForFile(file, project)).filter(Objects::nonNull).collect(Collectors.toList());
                // Make unique
                modules = new ArrayList<>(new HashSet<>(modules));

            }
        }
        return modules;
    }
}
