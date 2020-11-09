package ru.poidem.intellij.plugins.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
@State(
        name = "JPAMappingSettings",
        storages = {
                @Storage("JPAMappingSettings.xml")}
)
public class JPAMappingSettings implements PersistentStateComponent<JPAMappingSettings> {
    private List<ConfigurableJPAMapping> jpaMappings;

    public static List<ConfigurableJPAMapping> getPredefinedJPAMappings() {
        return Arrays.asList(
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "CHAR", "String", "CHAR",null),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "DATE", "Date","java.sql.Date","@Temporal(TemporalType.TIMESTAMP)"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "NUMBER", "BigDecimal","java.math.BigDecimal",null),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "INTEGER", "Long",null,null),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "VARCHAR2", "String", null,null),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "CLOB", "Clob", "java.sql.Clob","@Lob"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "BLOB", "Blob", "java.sql.Blob","@Lob")
        );
    }

    public JPAMappingSettings() {
        jpaMappings = new ArrayList<>(getPredefinedJPAMappings());
    }

    @Nullable
    @Override
    public JPAMappingSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JPAMappingSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<ConfigurableJPAMapping> getJpaMappings() {
        return jpaMappings;
    }

    public void setJpaMappings(List<ConfigurableJPAMapping> jpaMappings) {
        this.jpaMappings = jpaMappings;
    }
}
