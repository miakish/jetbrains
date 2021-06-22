package ru.poidem.intellij.plugins.util;

import com.intellij.database.model.DasArgument;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbPackage;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.twelvemonkeys.util.LinkedSet;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import ru.poidem.intellij.plugins.ui.ConfigurableJPAMapping;
import ru.poidem.intellij.plugins.ui.DBMSFamily;
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intellij.database.model.DasRoutine.Kind.FUNCTION;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class Util {
    public static VirtualFile lastChoosedFile;

    public static LinkedSet<Field> getFields(DbTable dbTable, JPAMappingSettings jpaMappingSettings) {
        LinkedSet<Field> fields = new LinkedSet<>();
        List<ConfigurableJPAMapping> jpaMappings = jpaMappingSettings.getJpaMappings();
        for (DasColumn column : DasUtil.getColumns(dbTable)) {
            Field field = new Field();
            field.setName(column.getName());
            field.setAutoGenerated(DasUtil.isAutoGenerated(column));
            field.setSQLType(column.getDataType());
            String typeName = column.getDataType().typeName;
            if("Oracle".equals(dbTable.getDataSource().getDatabaseVersion().name)) {
                if("NUMBER".equals(typeName)) {
                    if (column.getDataType().scale > 0) {
                        typeName = "NUMBER";
                    } else {
                        typeName = "INTEGER";
                    }
                }
            }
            String finalTypeName = typeName;
            ConfigurableJPAMapping configurableJPAMapping = jpaMappings.stream()
                    .filter(c -> dbTable.getDataSource().getDatabaseVersion().name.equals(c.getFamily().getName()) &&
                            finalTypeName.equals(c.getSqlDataType()))
                    .findAny()
                    .orElse(null);

            if (null != configurableJPAMapping) {
                field.setJavaType(configurableJPAMapping.getJavaDataType());
                field.setJavaImportType(configurableJPAMapping.getJavaImportType());
                field.setJavaAnnotation(configurableJPAMapping.getJavaAnnotation());
                field.setScale(column.getDataType().scale);
                field.setColumnDefinition(configurableJPAMapping.getJavaColumnDefinition());
            } else {
                field.setJavaType(null);
                field.setColumnDefinition(null);
            }
            field.setPrimary(DasUtil.isPrimary(column));
            fields.add(field);
        }
        return fields;
    }

    public static Arg getArg(DasArgument a) {
        Arg arg = new Arg();
        arg.setName(a.getName());
        arg.setSQLType(a.getDataType());
        return arg;
    }

    public static void loadJavaType(Arg a, DbPackage dbPackage, JPAMappingSettings jpaMappingSettings) {
        String typeName = a.getSQLType().typeName;
        List<ConfigurableJPAMapping> jpaMappings = jpaMappingSettings.getJpaMappings();
        if("Oracle".equals(dbPackage.getDataSource().getDatabaseVersion().name)) {
            if("NUMBER".equals(typeName)) {
                if (a.getSQLType().scale > 0) {
                    typeName = "NUMBER";
                } else {
                    typeName = "INTEGER";
                }
            }
        }
        String finalTypeName = typeName;
        ConfigurableJPAMapping configurableJPAMapping = jpaMappings.stream()
                .filter(c -> dbPackage.getDataSource().getDatabaseVersion().name.equals(c.getFamily().getName()) &&
                        finalTypeName.equals(c.getSqlDataType()))
                .findAny()
                .orElse(null);
        if(configurableJPAMapping!=null) {
            a.setJavaType(configurableJPAMapping.getJavaDataType());
            a.setJavaImportType(configurableJPAMapping.getJavaImportType());
        }
    }

    public static PackageInfo loadPackageInfo(DbPackage dbPackage, JPAMappingSettings jpaMappingSettings){
        PackageInfo packageInfo = new PackageInfo(dbPackage);
        for (Routine r:packageInfo.getRoutines()) {
            if (r.getType()==FUNCTION) {
                loadJavaType(r.getReturnArg(), dbPackage,jpaMappingSettings);
            }
            for (Arg a:r.getArgs()) {
                loadJavaType(a, dbPackage,jpaMappingSettings);
            }
        }
        return packageInfo;
    }

    public static void addGetterSetter(Set<Field> fields, StringBuilder javaTextFile) {
        for (Field field : fields) {
            javaTextFile.append("\n");

            javaTextFile.append("    public ").append(field.getJavaType()).append(" get").append(javaName(field.getName(), true)).append("() {").append("\n");
            javaTextFile.append("        return this.").append(javaName(field.getName(), false)).append(";").append("\n");
            javaTextFile.append("    }").append("\n");

            javaTextFile.append("\n");

            javaTextFile.append("    public void set").append(javaName(field.getName(), true)).append("(").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(") {").append("\n");
            javaTextFile.append("        this.").append(javaName(field.getName(), false)).append(" = ").append(javaName(field.getName(), false)).append(";").append("\n");
            javaTextFile.append("    }").append("\n");
        }

    }

    public static String javaName(String str, Boolean capitalizeFirstLetter) {
        String[] strings = NameUtil.splitNameIntoWords(str);
        StringBuilder name = new StringBuilder();

        for (int i = 0; strings.length > i; i++) {
            if (i == 0) {
                if (capitalizeFirstLetter) {
                    name.append(convertToTitleCaseIteratingChars(strings[i]));
                } else {
                    name.append(strings[i].toLowerCase());
                }
            } else {
                name.append(convertToTitleCaseIteratingChars(strings[i]));
            }
        }
        return name.toString();
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    public static boolean isDatabaseSupported(PsiElement[] psiElements) {
        if (psiElements[0] instanceof DbTable) {
            return Util.getDatabases().contains(((DbTable) psiElements[0]).getDataSource().getDatabaseVersion().name);
        } else if (psiElements[0] instanceof DbPackage) {
            return Util.getDatabases().contains(((DbPackage) psiElements[0]).getDataSource().getDatabaseVersion().name);
        } else{
            return false;
        }
    }

    public static void checkActionVisibility(@NotNull AnActionEvent anActionEvent, String actionText) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        if (psiElements[0] instanceof DbTable) {
            if (isDatabaseSupported(psiElements)) {
                anActionEvent.getPresentation().setEnabled(true);
            } else {
                anActionEvent.getPresentation().setEnabled(false);
                anActionEvent.getPresentation().setText(String.format("%s : database not supported", actionText));
            }
        } else {
            anActionEvent.getPresentation().setEnabled(false);
            anActionEvent.getPresentation().setText(String.format("%s : please, select a table", actionText));
        }
    }

    public static void checkDaoActionVisibility(@NotNull AnActionEvent anActionEvent, String actionText) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        if (psiElements[0] instanceof DbPackage) {
            if (isDatabaseSupported(psiElements)) {
                anActionEvent.getPresentation().setEnabled(true);
            } else {
                anActionEvent.getPresentation().setEnabled(false);
                anActionEvent.getPresentation().setText(String.format("%s : database not supported", actionText));
            }
        } else {
            anActionEvent.getPresentation().setEnabled(false);
            anActionEvent.getPresentation().setText(String.format("%s : please, select a package", actionText));
        }
    }

    public static Set<String> getDatabases() {
        DBMSFamily[] dbmsFamilies = DBMSFamily.values();
        Set<String> names = new HashSet<>();
        for (DBMSFamily dbmsFamily : dbmsFamilies) {
            names.add(dbmsFamily.getName());
        }
        return names;
    }

    public static String getGitBranch(Project project) {
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(project);
        GitRepository repository = manager.getRepositoryForRootQuick(ProjectUtil.guessProjectDir(project));
        if(repository!=null) {
            return repository.getCurrentBranch().getName();
        } else {
            return "";
        }
    }
}
