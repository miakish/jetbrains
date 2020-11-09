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
public abstract class DBMS {
    @NotNull
    public abstract String getSqlDataType();

    @NotNull
    public abstract UUID getId();

    @NotNull
    public abstract DBMSFamily getFamily();

    @NotNull
    public abstract Icon getIcon();

    @NotNull
    public abstract String getJavaDataType();

    @Nullable
    public abstract String getJavaColumnDefinition();

    @Nullable
    public abstract String getJavaImportType();
}
