package ru.poidem.intellij.plugins.action;

import com.intellij.database.psi.DbPackage;
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
import icons.DatabaseIcons;
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.poidem.intellij.plugins.util.Arg;
import ru.poidem.intellij.plugins.util.PackageInfo;
import ru.poidem.intellij.plugins.util.Routine;
import ru.poidem.intellij.plugins.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.database.model.DasRoutine.Kind.FUNCTION;
import static ru.poidem.intellij.plugins.util.Util.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class DAO extends AbstractAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        myActionPerformed(anActionEvent, DbPackage.class);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        updateAction(anActionEvent, DatabaseIcons.Package);
    }

    @Override
    public void fillAdditionalProperties(PsiElement psiElement, Map<String, String> additionalProperties, JPAMappingSettings jpaMappingSettings) {
        PackageInfo packageInfo = Util.loadPackageInfo((DbPackage) psiElement, jpaMappingSettings);
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

        StringBuilder fields = new StringBuilder();

        for (Routine r:packageInfo.getRoutines()) {

            if(StringUtils.isNotEmpty(r.getComment())) {
                fields.append("    /**").append("\n");
                fields.append("     * ").append(r.getComment()).append("\n");
                fields.append("     */").append("\n");
            }
            if (r.getType()==FUNCTION) {
                fields.append("    ").append(r.getReturnArg().getJavaType()!=null?r.getReturnArg().getJavaType():r.getReturnArg().getSQLType().typeName)
                        .append(" ").append(javaName(r.getName(), false)).append("(");
            } else {
                fields.append("    ").append("void ").append(javaName(r.getName(), false)).append("(");
            }
            for (int i=0; i<r.getArgs().size();i++) {
                Arg a=r.getArgs().get(i);
                fields.append(a.getJavaType()!=null?a.getJavaType():a.getSQLType().typeName)
                        .append(" ").append(javaName(a.getName(), false));
                if(i<(r.getArgs().size()-1)) {
                    fields.append(", ");
                }
            }
            fields.append(");").append("\n");
        }

        additionalProperties.put("FIELDS", fields.toString());
    }

    @Override
    public List<PsiClass> getPsiClassList(PsiDirectory psiDirectory, Map<String, String> additionalProperties) {
        List<PsiClass> psiClassList = new ArrayList<>();
        psiClassList.add(JavaDirectoryService.getInstance().createClass(psiDirectory, className, "PG_PackageDao.java", true, additionalProperties));
        psiClassList.add(JavaDirectoryService.getInstance().createClass(psiDirectory, className, "PG_PackageDaoImpl.java", true, additionalProperties));
        return psiClassList;
    }
}
