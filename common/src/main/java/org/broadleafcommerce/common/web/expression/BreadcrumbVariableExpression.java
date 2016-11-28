/**
 * 
 */
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.TemplatingExistCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blBreadcrumbVariableExpression")
@Conditional(TemplatingExistCondition.class)
public class BreadcrumbVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;
    
    @Override
    public String getName() {
        return "breadcrumbs";
    }

    public List<BreadcrumbDTO> getBreadcrumbs() {
        String baseUrl = getBaseUrl();
        Map<String, String[]> params = getParams();
        return breadcrumbService.buildBreadcrumbDTOs(baseUrl, params);
    }

    protected String getBaseUrl() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            return brc.getRequest().getRequestURI();
        }
        return "";
    }
    
    protected Map<String, String[]> getParams() {
        Map<String, String[]> paramMap = new HashMap<>();
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            paramMap = BroadleafRequestContext.getRequestParameterMap();
            if (paramMap != null) {
                paramMap = new HashMap<>(paramMap);
            }
        }
        return paramMap;
    }
}
