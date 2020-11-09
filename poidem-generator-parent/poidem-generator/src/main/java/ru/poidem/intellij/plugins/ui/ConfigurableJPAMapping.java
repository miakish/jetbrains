package ru.poidem.intellij.plugins.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class ConfigurableJPAMapping extends DBMS {
    private final UUID id;
    @NotNull
    private DBMSFamily family;
    @NotNull
    private String sqlDataType;
    @NotNull
    private String javaDataType;
    @Nullable
    private String javaImportType;
    @Nullable
    private String javaAnnotation;
    @Nullable
    private String javaColumnDefinition;

    ConfigurableJPAMapping() {
        this(UUID.randomUUID(), DBMSFamily.ORACLE);
    }

    ConfigurableJPAMapping(@NotNull UUID id, @NotNull DBMSFamily family) {
        this(id, family, family.getSqlDataType(), family.getJavaDataType(), family.getJavaImportType(), family.getJavaAnnotation(), family.getJavaColumnDefinition());
    }

    ConfigurableJPAMapping(@NotNull UUID id, @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType,
                           @Nullable String javaImportType,
                           @Nullable String javaAnnotation) {
        this(id, family, sqlDataType, javaDataType, javaImportType, javaAnnotation, null);
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType,
                           @Nullable String javaImportType,
                           @Nullable String javaAnnotation,
                           @Nullable String javaColumnDefinition) {
        this.id = id;
        this.family = family;
        this.sqlDataType = sqlDataType;
        this.javaDataType = javaDataType;
        this.javaImportType = javaImportType;
        this.javaAnnotation = javaAnnotation;
        this.javaColumnDefinition = javaColumnDefinition;
    }

    public void setSqlDataType(@NotNull String value) {
        sqlDataType = value;
    }

    public void setJavaDataType(@NotNull String value) {
        javaDataType = value;
    }

    public void setJavaColumnDefinition(@Nullable String value) {
        javaColumnDefinition = value;
    }

    public void setFamily(@NotNull DBMSFamily value) {
        family = value;
    }
    public void setJavaImportType(@Nullable String javaImportType) {
        this.javaImportType = javaImportType;
    }

    @Nullable
    public String getJavaImportType() {
        return javaImportType;
    }
    @NotNull
    @Override
    public String getSqlDataType() {
        return sqlDataType;
    }

    @NotNull
    @Override
    public String getJavaDataType() {
        return javaDataType;
    }

    @Nullable
    @Override
    public String getJavaColumnDefinition() {
        return javaColumnDefinition;
    }

    @NotNull
    @Override
    public UUID getId() {
        return id;
    }

    @NotNull
    @Override
    public DBMSFamily getFamily() {
        return family;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return family.getIcon();
    }

    @Nullable
    public String getJavaAnnotation() {
        return javaAnnotation;
    }

    public void setJavaAnnotation(@Nullable String javaAnnotation) {
        this.javaAnnotation = javaAnnotation;
    }

}
