package ru.poidem.intellij.plugins.util;

import com.intellij.database.model.DataType;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class Arg {

    private String name;
    private DataType SQLType;
    private String javaType;
    private String javaImportType;

    public DataType getSQLType() {
        return SQLType;
    }

    public void setSQLType(DataType SQLType) {
        this.SQLType = SQLType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaImportType() {
        return javaImportType;
    }

    public void setJavaImportType(String javaImportType) {
        this.javaImportType = javaImportType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
