package ru.poidem.intellij.plugins.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.JavaCreateTemplateInPackageAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.ui.IconManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class PoidemEntity extends JavaCreateTemplateInPackageAction<PsiClass> {

    public static final String ACTION_TITLE = "New Poidem file";

    public PoidemEntity() {
        super("Poidem Generator", "Creates a Poidem file from the specified template", IconManager.getInstance().getIcon("/META-INF/pluginIcon.svg", PoidemEntity.class),false);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {

        builder.setTitle(ACTION_TITLE)
            .addKind("poidem Class", AllIcons.Nodes.Class, "PG_Class.java")
            .addKind("poidem DbEnum Integer", AllIcons.Nodes.Enum, "PG_DbEnumInteger.java")
            .addKind("poidem DbEnum Long", AllIcons.Nodes.Enum, "PG_DbEnumLong.java")
            .addKind("poidem DbEnum String", AllIcons.Nodes.Enum, "PG_DbEnumString.java")
//            .addKind("Dto", AllIcons.Nodes.Class, "PG_Dto.java")
//            .addKind("DtoCol", AllIcons.Nodes.Class, "PG_DtoCol.java")
//            .addKind("Entity", AllIcons.Nodes.Class, "PG_Entity.java")
            .addKind("poidem Enum", AllIcons.Nodes.Enum, "PG_Enum.java")
            .addKind("poidem EnumConverter Integer", AllIcons.Nodes.Class, "PG_EnumConverterInteger.java")
            .addKind("poidem EnumConverter Long", AllIcons.Nodes.Class, "PG_EnumConverterLong.java")
            .addKind("poidem EnumConverter String", AllIcons.Nodes.Class, "PG_EnumConverterString.java")
            .addKind("poidem Interface", AllIcons.Nodes.Interface, "PG_Interface.java")
            .addKind("poidem Job", AllIcons.Nodes.Class, "PG_Job.java")
            .addKind("poidem PackageDao", AllIcons.Nodes.Interface, "PG_PackageDao.java")
            .addKind("poidem PackageDaoImpl", AllIcons.Nodes.Class, "PG_PackageDaoImpl.java")
            .addKind("poidem Service", AllIcons.Nodes.Interface, "PG_Service.java")
            .addKind("poidem ServiceImpl", AllIcons.Nodes.Class, "PG_ServiceImpl.java");
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Create Poidem file " + newName;
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull PsiClass createdElement) {
        return createdElement.getLBrace();
    }

    @Override
    protected final PsiClass doCreate(PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        return JavaDirectoryService.getInstance().createClass(dir, className, templateName, true);
    }

}
