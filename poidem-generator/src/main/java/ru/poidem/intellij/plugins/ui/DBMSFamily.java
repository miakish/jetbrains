package ru.poidem.intellij.plugins.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Iconable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public enum DBMSFamily implements Iconable {
    ORACLE("Oracle", AllIcons.Providers.Oracle, "VARCHAR2", "String",null, null,null);

    private final String name;
    private final Icon icon;
    private final String sqlDataType;
    private final String javaDataType;
    private final String javaImportType;
    private final String javaAnnotation;
    private final String javaColumnDefinition;

    DBMSFamily(@NotNull String name,
               @NotNull Icon icon,
               @NotNull String sqlDataType,
               @NotNull String javaDataType,
               @Nullable String javaImportType,
               @Nullable String javaAnnotation,
               @Nullable String javaColumnDefinition) {
        this.name = name;
        this.icon = icon;
        this.sqlDataType = sqlDataType;
        this.javaDataType = javaDataType;
        this.javaImportType = javaImportType;
        this.javaAnnotation = javaAnnotation;
        this.javaColumnDefinition = javaColumnDefinition;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Icon getIcon(@IconFlags int flags) {
        return getIcon();
    }

    public String getSqlDataType() {
        return sqlDataType;
    }

    public String getJavaDataType() {
        return javaDataType;
    }

    public String getJavaColumnDefinition() {
        return javaColumnDefinition;
    }
    public String getJavaImportType() {
        return javaImportType;
    }
    public String getJavaAnnotation() {
        return javaAnnotation;
    }
}
