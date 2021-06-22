package ru.poidem.intellij.plugins.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class JPAMappingConfigurable implements Configurable {
    private final JPAMappingSettings jpaMappingSettings;
    private JPAMappingPanel jpaMappingPanel;

    public JPAMappingConfigurable(Project project) {
        this.jpaMappingSettings = ServiceManager.getService(project, JPAMappingSettings.class);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "JPA Mapping";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == jpaMappingPanel) {
            jpaMappingPanel = new JPAMappingPanel();
        }
        return jpaMappingPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return !jpaMappingPanel.getJpaMappingEditor().getModel().getItems().equals(jpaMappingSettings.getJpaMappings());
    }

    @Override
    public void apply() throws ConfigurationException {
        jpaMappingSettings.setJpaMappings(jpaMappingPanel.getJpaMappingEditor().apply());
    }

    public void reset() {
        jpaMappingPanel.getJpaMappingEditor().reset(jpaMappingSettings.getJpaMappings());
    }
}
