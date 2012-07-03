package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.site.domain.Theme;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.util.Validate;

/**
 * Overrides the Thymeleaf ContextTemplateResolver and appends the org.broadleafcommerce.common.web.Theme path to the url
 * if it exists.
 */
public class BroadleafThymeleafServletContextTemplateResolver extends ServletContextTemplateResolver {    

    @Override
    protected String computeResourceName(final TemplateProcessingParameters templateProcessingParameters) {
        String themePath = null;
    
        Theme theme = BroadleafRequestContext.getBroadleafRequestContext().getTheme();
        if (theme != null && theme.getPath() != null) {
            themePath = theme.getPath();
        }             

        checkInitialized();

        final String templateName = templateProcessingParameters.getTemplateName();

        Validate.notNull(templateName, "Template name cannot be null");

        String unaliasedName = this.getTemplateAliases().get(templateName);
        if (unaliasedName == null) {
            unaliasedName = templateName;
        }

        final StringBuilder resourceName = new StringBuilder();
        String prefix = this.getPrefix();
        if (prefix != null && ! prefix.trim().equals("")) {
           
            if (themePath != null) {        
                resourceName.append(prefix).append(themePath);
            }
        }
        resourceName.append(unaliasedName);
        String suffix = this.getSuffix();
        if (suffix != null && ! suffix.trim().equals("")) {
            resourceName.append(suffix);
        }

        return resourceName.toString();
    }
    
}


