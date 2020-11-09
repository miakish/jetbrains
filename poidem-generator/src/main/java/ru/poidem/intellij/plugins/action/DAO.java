package ru.poidem.intellij.plugins.action;

import com.intellij.database.psi.DbPackage;
import com.intellij.icons.AllIcons;
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
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import ru.poidem.intellij.plugins.ui.PoidemSettings;
import ru.poidem.intellij.plugins.util.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.poidem.intellij.plugins.util.Arg;
import ru.poidem.intellij.plugins.util.PackageInfo;
import ru.poidem.intellij.plugins.util.Routine;
import ru.poidem.intellij.plugins.util.Util;

import java.util.HashMap;
import java.util.Map;

import static ru.poidem.intellij.plugins.util.Util.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class DAO extends AnAction {
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
            if (!(psiElement instanceof DbPackage)) {
                continue;
            }

            PackageInfo packageInfo = Util.loadPackageInfo((DbPackage) psiElement, jpaMappingSettings);
            VirtualFile chooseFile = ProjectUtil.guessProjectDir(project);
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            if (null != lastChoosedFile) {
                chooseFile = lastChoosedFile;
            }
            lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (null == lastChoosedFile) {
                return;
            }

            Map<String, String> additionalProperties = new HashMap<>();


            StringBuilder importsField = new StringBuilder();
            Map<String,String> imports1 = new HashMap<>();
            for (Routine r:packageInfo.getRoutines()) {
                for (Arg a:r.getArgs()) {
                    if(StringUtils.isNotEmpty(a.getJavaImportType()) && !imports1.containsKey(a.getJavaImportType())) {
                        importsField.append("import ").append(a.getJavaImportType()).append(";").append("\n");
                        imports1.put(a.getJavaImportType(), "");
                    }
                }
            }
            additionalProperties.put("IMPORTS", importsField.toString());

            StringBuilder packageName = new StringBuilder();
            packageName.append(packageInfo.getSchema()).append(".").append(packageInfo.getName());
            additionalProperties.put("PackageName", packageName.toString());

//            for (Routine r:packageInfo.getRoutines()) {
//                javaTextFile1.append("\n");
//                if(StringUtils.isNotEmpty(r.getComment())) {
//                    javaTextFile1.append("    /**").append("\n");
//                    javaTextFile1.append("     * ").append(r.getComment()).append("\n");
//                    javaTextFile1.append("     */").append("\n");
//                }
//                if (r.getType()==FUNCTION) {
//                    javaTextFile1.append("    ").append(r.getReturnArg().getJavaType())
//                            .append(" ").append(r.getName()).append("(");
//                } else {
//                    javaTextFile1.append("    ").append("void ").append(r.getName()).append("(");
//                }
//                for (int i=0; i<r.getArgs().size();i++) {
//                    Arg a=r.getArgs().get(i);
//                    javaTextFile1.append(a.getJavaType()).append(" ").append(a.getName());
//                    if(i<(r.getArgs().size()-1)) {
//                        javaTextFile1.append(", ");
//                    }
//                }
//                javaTextFile1.append(");").append("\n");
//            }

            String className = javaName(packageInfo.getName(), true);
            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(lastChoosedFile);

            PsiClass psiClass1 = JavaDirectoryService.getInstance().createClass(psiDirectory, className, "PG_PackageDao.java", true, additionalProperties);
            PsiClass psiClass2 = JavaDirectoryService.getInstance().createClass(psiDirectory, className, "PG_PackageDaoImpl.java", true, additionalProperties);

            Runnable r = () -> psiDirectory.add(psiClass1).add(psiClass2);
            WriteCommandAction.runWriteCommandAction(project, r);

        }
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        if (actionText.isEmpty()) {
            actionText = anActionEvent.getPresentation().getText();
        }
        anActionEvent.getPresentation().setIcon(AllIcons.Nodes.Package);
        checkDaoActionVisibility(anActionEvent, actionText);
        super.update(anActionEvent);
    }
}
