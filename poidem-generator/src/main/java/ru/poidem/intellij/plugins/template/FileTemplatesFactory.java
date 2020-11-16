package ru.poidem.intellij.plugins.template;

import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.ui.IconManager;
import icons.DatabaseIcons;
import icons.JavaUltimateIcons;

import javax.swing.*;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class FileTemplatesFactory implements FileTemplateGroupDescriptorFactory {

  public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
    Icon i = IconManager.getInstance().getIcon("/META-INF/pluginIcon.svg", FileTemplatesFactory.class);
    FileTemplateGroupDescriptor descriptor = new FileTemplateGroupDescriptor("Poidem Generator", i);
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Class.java", AllIcons.Nodes.Class));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_DbEnumInteger.java", AllIcons.Nodes.Enum));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_DbEnumLong.java", AllIcons.Nodes.Enum));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_DbEnumString.java", AllIcons.Nodes.Enum));

    descriptor.addTemplate(new FileTemplateDescriptor("PG_Dto.java", DatabaseIcons.Col));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Entity.java", JavaUltimateIcons.Javaee.EntityBean));

    descriptor.addTemplate(new FileTemplateDescriptor("PG_Enum.java", AllIcons.Nodes.Enum));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_EnumConverterInteger.java", JavaUltimateIcons.Jsf.Converter));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_EnumConverterLong.java", JavaUltimateIcons.Jsf.Converter));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_EnumConverterString.java", JavaUltimateIcons.Jsf.Converter));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Interface.java", AllIcons.Nodes.Interface));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Job.java", AllIcons.Actions.BuildAutoReloadChanges));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_PackageDao.java", AllIcons.Nodes.Interface));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_PackageDaoImpl.java", AllIcons.Nodes.Class));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Service.java", AllIcons.Nodes.Interface));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_ServiceImpl.java", AllIcons.Nodes.Class));

    descriptor.addTemplate(new FileTemplateDescriptor("PG_Controller.java", AllIcons.Nodes.Controller));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_Validator.java", JavaUltimateIcons.Jsf.Validator));
    descriptor.addTemplate(new FileTemplateDescriptor("PG_RestController.java", AllIcons.Nodes.Controller));
    return descriptor;
  }

}