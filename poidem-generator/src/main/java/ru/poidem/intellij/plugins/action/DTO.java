package ru.poidem.intellij.plugins.action;

import com.intellij.database.psi.DbTable;
import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import git4idea.branch.GitBranchUtil;
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import ru.poidem.intellij.plugins.ui.PoidemSettings;
import ru.poidem.intellij.plugins.util.Field;
import ru.poidem.intellij.plugins.util.TableInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.poidem.intellij.plugins.util.Util.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class DTO extends AnAction {
    private String actionText = StringUtils.EMPTY;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }

        final JPAMappingSettings jpaMappingSettings = ServiceManager.getService(project, JPAMappingSettings.class);
        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                continue;
            }

            TableInfo tableInfo = new TableInfo((DbTable) psiElement);
            VirtualFile chooseFile = ProjectUtil.guessProjectDir(project);
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            if (null != lastChoosedFile) {
                chooseFile = lastChoosedFile;
            }
            lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (null == lastChoosedFile) {
                return;
            }

            Set<Field> fields = getFields((DbTable) psiElement, jpaMappingSettings);

            StringBuilder importsField = new StringBuilder();
            Map<String,String> imports = new HashMap<>();
            for (Field field : fields) {
                if(StringUtils.isNotEmpty(field.getJavaImportType()) && !imports.containsKey(field.getJavaImportType()))
                {
                    importsField.append("import ").append(field.getJavaImportType()).append(";").append("\n");
                    imports.put(field.getJavaImportType(),"");
                }
            }

            Map<String, String> additionalProperties = new HashMap<>();
            additionalProperties.put("IMPORTS", importsField.toString());
            additionalProperties.put("COMMENT", tableInfo.getTableComment());
            additionalProperties.put("GIT_BRANCH", GitBranchUtil.getCurrentRepository(project).getCurrentBranch().getName());

            StringBuilder columnFields = new StringBuilder();
            Map<String, String> comments = tableInfo.getColumsComment();
            for (Field field : fields) {
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(comments.get(field.getName()))) {
                    columnFields.append("    /**").append(comments.get(field.getName())).append("*/").append("\n");
                }
                columnFields.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                columnFields.append("\n");
            }
            additionalProperties.put("FIELDS", columnFields.toString());

            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(lastChoosedFile);
            PsiClass psiClass = JavaDirectoryService.getInstance().createClass(psiDirectory, javaName(tableInfo.getTableName(), true), "PG_Dto.java", true, additionalProperties);

            Runnable r = () -> psiDirectory.add(psiClass);

            WriteCommandAction.runWriteCommandAction(project, r);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        if (actionText.isEmpty()) {
            actionText = anActionEvent.getPresentation().getText();
        }
        anActionEvent.getPresentation().setIcon(AllIcons.Nodes.DataTables);
        checkActionVisibility(anActionEvent, actionText);
        super.update(anActionEvent);
    }
}
