package ru.poidem.intellij.plugins.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import icons.DatabaseIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.poidem.intellij.plugins.ui.JPAMappingSettings;
import ru.poidem.intellij.plugins.ui.PoidemSettings;
import ru.poidem.intellij.plugins.util.Field;
import ru.poidem.intellij.plugins.util.TableInfo;

import java.util.*;

import static ru.poidem.intellij.plugins.util.Util.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class DTO extends AbstractAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        myActionPerformed(anActionEvent, DbTable.class);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        updateAction(anActionEvent, DatabaseIcons.Col);
        checkActionVisibility(anActionEvent, actionText);
        super.update(anActionEvent);
    }

    @Override
    public void fillAdditionalProperties(PsiElement psiElement, Map<String, String> additionalProperties, JPAMappingSettings jpaMappingSettings, PoidemSettings poidemSettings) {
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
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
        additionalProperties.put("IMPORTS", importsField.toString());
        if(StringUtils.isNotBlank(tableInfo.getTableComment())) {
            additionalProperties.put("COMMENT", tableInfo.getTableComment());
        }
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
    }

    @Override
    public List<PsiClass> loadTemplates(PsiElement psiElement, PsiDirectory psiDirectory, Map<String, String> additionalProperties, JPAMappingSettings jpaMappingSettings) {
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String className = javaName(tableInfo.getTableName(), true);
        List<PsiClass> psiClassList = new ArrayList<>();
        psiClassList.add(JavaDirectoryService.getInstance().createClass(psiDirectory, className, "PG_Dto.java", true, additionalProperties));
        return psiClassList;
    }
}
