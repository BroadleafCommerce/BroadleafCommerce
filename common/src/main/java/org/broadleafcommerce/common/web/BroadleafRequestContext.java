/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.web;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Convenient holder class for various objects to be automatically available on thread local without invoking the various
 * services yourself
 * 
 * @see {@link BroadleafRequestProcessor}
 */
public class BroadleafRequestContext {
    
    protected static final Log LOG = LogFactory.getLog(BroadleafRequestContext.class);
    
    private static final ThreadLocal<BroadleafRequestContext> BROADLEAF_REQUEST_CONTEXT = ThreadLocalManager.createThreadLocal(BroadleafRequestContext.class);
    
    public static BroadleafRequestContext getBroadleafRequestContext() {
        return BROADLEAF_REQUEST_CONTEXT.get();
    }
    
    public static void setBroadleafRequestContext(BroadleafRequestContext broadleafRequestContext) {
        BROADLEAF_REQUEST_CONTEXT.set(broadleafRequestContext);
    }

    public static boolean hasLocale(){
        if (getBroadleafRequestContext() != null) {
            if(getBroadleafRequestContext().getLocale() != null){
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasCurrency() {
        if (getBroadleafRequestContext() != null) {
            if (getBroadleafRequestContext().getBroadleafCurrency() != null) {
                return true;
            }
        }
        return false;
    }

    public static BroadleafCurrency getCurrency() {
        BroadleafCurrency returnCurrency = null;
        if (getBroadleafRequestContext() != null) {
            returnCurrency = getBroadleafRequestContext().getBroadleafCurrency();
        }

        if (returnCurrency == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("BroadleafRequestContext.getCurrency() called but returned null");
            }
        }
        return returnCurrency;
    }

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected WebRequest webRequest;
    protected SandBox sandBox;
    protected Locale locale;
    protected TimeZone timeZone;
    protected BroadleafCurrency broadleafCurrency;
    protected BroadleafCurrency requestedCurrency;
    protected Site site;
    protected Theme theme;
    protected java.util.Locale javaLocale;
    protected Currency javaCurrency;
    protected Catalog currentCatalog;
    protected Site currentProfile;
    protected Boolean ignoreSite = false;
    protected Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected MessageSource messageSource;
    protected RequestDTO requestDTO;
    protected Boolean isAdmin = false;
    protected Long adminUserId;

    protected DeployState deployState = DeployState.UNDEFINED;
    protected DeployBehavior deployBehavior = DeployBehavior.UNDEFINED;
    protected Boolean internalIgnoreFilters = false;
    protected ValidateProductionChangesState validateProductionChangesState = ValidateProductionChangesState.UNDEFINED;
    protected EnforceEnterpriseCollectionBehaviorState enforceEnterpriseCollectionBehaviorState = EnforceEnterpriseCollectionBehaviorState.UNDEFINED;

    /**
     * Gets the current request on the context
     * @return
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Provide easy access to Request Attributes without introducing a tight dependency on the HttpRequest.
     * @return
     */
    public Object getRequestAttribute(String name) {
        Object param = null;
        if (getRequest() != null) {
            param = getRequest().getAttribute(name);
        }
        return param;
    }

    /**
     * Sets the current request on the context. Note that this also invokes {@link #setWebRequest(WebRequest)} by wrapping
     * <b>request</b> in a {@link ServletWebRequest}.
     * 
     * @param request
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
        this.webRequest = new ServletWebRequest(request);
    }

    /**
     * Returns the response for the context
     * 
     * @return
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Sets the response on the context
     * 
     * @param response
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * Sets the generic request on the context. This is available to be used in non-Servlet environments (like Portlets).
     * Note that if <b>webRequest</b> is an instance of {@link ServletWebRequest} then
     * {@link #setRequest(HttpServletRequest)} will be invoked as well with the native underlying {@link HttpServletRequest}
     * passed as a parameter.
     * <br />
     * <br />
     * Also, if <b>webRequest</b> is an instance of {@link ServletWebRequest} then an attempt is made to set the response
     * (note that this could be null if the ServletWebRequest was not instantiated with both the {@link HttpServletRequest}
     * and {@link HttpServletResponse}
     * @param webRequest
     */
    public void setWebRequest(WebRequest webRequest) {
        this.webRequest = webRequest;
        if (webRequest instanceof ServletWebRequest) {
            this.request = ((ServletWebRequest) webRequest).getRequest();
            setResponse(((ServletWebRequest) webRequest).getResponse());
        }
    }

    /**
     * Returns the generic request for use outside of servlets (like in Portlets). This will be automatically set
     * by invoking {@link #setRequest(HttpServletRequest)}
     * 
     * @return the generic request
     * @see {@link #setWebRequest(WebRequest)}
     */
    public WebRequest getWebRequest() {
        return webRequest;
    }

    /**
     * Returns a Site instance that is not attached to any Hibernate session
     * @return
     * @deprecated this has been changed to {@link #getNonPersistentSite()} to explicitly indicate that the site
     * being returned is not attached to a Hibernate session
     */
    @Deprecated
    public Site getSite() {
        return getNonPersistentSite();
    }

    /**
     * @deprecated this has been changed to {@link #setNonPersistentSite()} to explicitly indicate that the site being set
     * is not attached to an active Hibernate session
     */
    @Deprecated
    public void setSite(Site site) {
        setNonPersistentSite(site);
    }
    
    /**
     * @return the site that is currently associated to this request thread. The site that is returned is not attached to a
     * Hibernate session and thus cannot lazy-load collection properties. For additional collections that are added to
     * extensions of {@link Site}, they should be manually cloned by overriding the clone() method.
     */
    public Site getNonPersistentSite() {
        return site;
    }
    
    /**
     * 
     */
    public void setNonPersistentSite(Site site) {
        this.site = site;
    }

    public SandBox getSandBox() {
        return sandBox;
    }

    public Long getSandBoxId() {
        if (sandBox != null) {
            return sandBox.getId();
        }
        return null;
    }

    public boolean isProductionSandBox() {
        return sandBox == null || SandBoxType.PRODUCTION == sandBox.getSandBoxType();
    }

    public void setSandBox(SandBox sandBox) {
        this.sandBox = sandBox;
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the java.util.Locale constructed from the org.broadleafcommerce.common.locale.domain.Locale.
     * @return
     */
    public java.util.Locale getJavaLocale() {
        if (this.javaLocale == null) {
            this.javaLocale = convertLocaleToJavaLocale();
        }
        return this.javaLocale;
    }

    /**
     * Returns the java.util.Currency constructed from the org.broadleafcommerce.common.currency.domain.BroadleafCurrency.
     * If there is no BroadleafCurrency specified this will return the currency based on the JVM locale
     * 
     * @return
     */
    public Currency getJavaCurrency() {
        if (javaCurrency == null) {
            try {
                if (getBroadleafCurrency() != null && getBroadleafCurrency().getCurrencyCode() != null) {
                    javaCurrency = Currency.getInstance(getBroadleafCurrency().getCurrencyCode());
                } else {
                    javaCurrency = Currency.getInstance(getJavaLocale());
                }
            } catch (IllegalArgumentException e) {
                LOG.warn("There was an error processing the configured locale into the java currency. This is likely because the default" +
                		" locale is set to something like 'en' (which is NOT apart of ISO 3166 and does not have a currency" +
                		" associated with it) instead of 'en_US' (which IS apart of ISO 3166 and has a currency associated" +
                		" with it). Because of this, the currency is now set to the default locale of the JVM");
                LOG.warn("To fully resolve this, update the default entry in the BLC_LOCALE table to take into account the" +
                		" country code as well as the language. Alternatively, you could also update the BLC_CURRENCY table" +
                		" to contain a default currency.");
                javaCurrency = Currency.getInstance(java.util.Locale.getDefault());
            }
        }
        return javaCurrency;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.javaLocale = convertLocaleToJavaLocale();
    }

    public String getRequestURIWithoutContext() {
        String requestURIWithoutContext = null;

        if (request != null && request.getRequestURI() != null) {
            if (request.getContextPath() != null) {
                requestURIWithoutContext = request.getRequestURI().substring(request.getContextPath().length());
            } else {
                requestURIWithoutContext = request.getRequestURI();
            }

            // Remove JSESSION-ID or other modifiers
            int pos = requestURIWithoutContext.indexOf(";");
            if (pos >= 0) {
                requestURIWithoutContext = requestURIWithoutContext.substring(0,pos);
            }
        }
        
        return requestURIWithoutContext;
        
    }
    
    protected java.util.Locale convertLocaleToJavaLocale() {      
        if (locale == null || locale.getLocaleCode() == null) {
            return java.util.Locale.getDefault();
        } else {
            return BroadleafRequestContext.convertLocaleToJavaLocale(locale);
        }
    }
    
    public static java.util.Locale convertLocaleToJavaLocale(Locale broadleafLocale) {
        if (broadleafLocale != null) {
            String localeString = broadleafLocale.getLocaleCode();
            return org.springframework.util.StringUtils.parseLocaleString(localeString);
        }
        return null;
    }
    
    public boolean isSecure() {
        boolean secure = false;
        if (request != null) {
             secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        }
        return secure;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public BroadleafCurrency getBroadleafCurrency() {
        return broadleafCurrency;
    }

    public void setBroadleafCurrency(BroadleafCurrency broadleafCurrency) {
        this.broadleafCurrency = broadleafCurrency;
    }

    public BroadleafCurrency getRequestedBroadleafCurrency() {
        return requestedCurrency;
    }

    public void setRequestedBroadleafCurrency(BroadleafCurrency requestedCurrency) {
        this.requestedCurrency = requestedCurrency;
    }

    public Catalog getCurrentCatalog() {
        return currentCatalog;
    }

    public void setCurrentCatalog(Catalog currentCatalog) {
        this.currentCatalog = currentCatalog;
    }

    public Site getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(Site currentProfile) {
        this.currentProfile = currentProfile;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String[]> getRequestParameterMap() {
        return getBroadleafRequestContext().getRequest().getParameterMap();
    }

    public Boolean getIgnoreSite() {
        return ignoreSite;
    }

    public void setIgnoreSite(Boolean ignoreSite) {
        this.ignoreSite = ignoreSite;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public RequestDTO getRequestDTO() {
        return requestDTO;
    }

    public void setRequestDTO(RequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

    public Boolean getAdmin() {
        return isAdmin == null ? false : isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    /**
     * Intended for internal use only
     */
    public Boolean getInternalIgnoreFilters() {
        return internalIgnoreFilters;
    }

    /**
     * Intended for internal use only
     */
    public void setInternalIgnoreFilters(Boolean internalIgnoreFilters) {
        this.internalIgnoreFilters = internalIgnoreFilters;
    }

    public DeployState getDeployState() {
        return deployState;
    }

    public void setDeployState(DeployState deployState) {
        this.deployState = deployState;
    }

    public DeployBehavior getDeployBehavior() {
        return deployBehavior;
    }

    public void setDeployBehavior(DeployBehavior deployBehavior) {
        this.deployBehavior = deployBehavior;
    }

    public ValidateProductionChangesState getValidateProductionChangesState() {
        return validateProductionChangesState;
    }

    public void setValidateProductionChangesState(ValidateProductionChangesState validateProductionChangesState) {
        this.validateProductionChangesState = validateProductionChangesState;
    }

    /**
     * Defines the state in which sandboxable collections in the Enterprise module should adhere to Broadleaf defined behavior.
     * When FALSE, {@link org.hibernate.collection.spi.PersistentCollection} extensions in the Enterprise module will delegate
     * to the standard Hibernate behavior. This is useful when the desire is to build and persist entity object structures (that
     * the Enterprise module would otherwise interpret as sandboxable) without interference from the Enterprise module
     * on the collection persistence behavior. When the Enterprise module is loaded, the behavior is enforced by default.
     *
     * @return the definition of how the enterprise module should handle persistent collection behavior
     */
    public EnforceEnterpriseCollectionBehaviorState getEnforceEnterpriseCollectionBehaviorState() {
        return enforceEnterpriseCollectionBehaviorState;
    }

    /**
     * Returns the state in which sandboxable collections in the Enterprise module should adhere to Broadleaf defined behavior.
     * When FALSE, {@link org.hibernate.collection.spi.PersistentCollection} extensions in the Enterprise module will delegate
     * to the standard Hibernate behavior. This is useful when the desire is to build and persist entity object structures (that
     * the Enterprise module would otherwise interpret as sandboxable) without interference from the Enterprise module
     * on the collection persistence behavior. When the Enterprise module is loaded, the behavior is enforced by default.
     *
     * @param enforceEnterpriseCollectionBehaviorState
     */
    public void setEnforceEnterpriseCollectionBehaviorState(EnforceEnterpriseCollectionBehaviorState
                                                                    enforceEnterpriseCollectionBehaviorState) {
        this.enforceEnterpriseCollectionBehaviorState = enforceEnterpriseCollectionBehaviorState;
    }

    /**
     * In some cases, it is useful to utilize a clone of the context that does not include the actual container request
     * and response information. Such a case would be when executing an asynchronous operation on a new thread from
     * an existing request thread. That new thread may still require context information, in which case this lightweight
     * context is useful.
     *
     * @return The instance without the container request and response
     */
    public BroadleafRequestContext createLightWeightClone() {
        BroadleafRequestContext context = new BroadleafRequestContext();
        context.setIgnoreSite(ignoreSite);
        context.setSandBox(sandBox);
        context.setNonPersistentSite(site);
        context.setEnforceEnterpriseCollectionBehaviorState(enforceEnterpriseCollectionBehaviorState);
        context.setAdmin(isAdmin);
        context.setAdminUserId(adminUserId);
        context.setBroadleafCurrency(broadleafCurrency);
        context.setCurrentCatalog(currentCatalog);
        context.setCurrentProfile(currentProfile);
        context.setDeployBehavior(deployBehavior);
        context.setDeployState(deployState);
        context.setInternalIgnoreFilters(internalIgnoreFilters);
        context.setLocale(locale);
        context.setMessageSource(messageSource);
        context.setTheme(theme);
        context.setValidateProductionChangesState(validateProductionChangesState);
        context.setTimeZone(timeZone);
        //purposefully excluding additionalProperties - this contains state that can mess with SandBoxFilterEnabler (for one)

        return context;
    }

    /**
     * In some cases, it is useful to create a JSON representation of the context that does not include the actual container
     * request and response information. This can be used subsequently to resurrect the BroadleafRequestContext state, presumably
     * on a new thread.
     *
     * @return
     */
    public String createLightWeightCloneJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ignoreSite\":\"");
        sb.append(ignoreSite==null?null:ignoreSite);
        sb.append("\",\"sandBox\":\"");
        sb.append(sandBox==null?null:sandBox.getId());
        sb.append("\",\"nonPersistentSite\":\"");
        sb.append(site==null?null:site.getId());
        sb.append("\",\"enforceEnterpriseCollectionBehaviorState\":\"");
        sb.append(enforceEnterpriseCollectionBehaviorState==null?null:enforceEnterpriseCollectionBehaviorState.toString());
        sb.append("\",\"admin\":\"");
        sb.append(isAdmin==null?null:isAdmin.toString());
        sb.append("\",\"adminUserId\":\"");
        sb.append(adminUserId==null?null:adminUserId);
        sb.append("\",\"broadleafCurrency\":\"");
        sb.append(broadleafCurrency==null?null:broadleafCurrency.getCurrencyCode());
        sb.append("\",\"currentCatalog\":\"");
        sb.append(currentCatalog==null?null:currentCatalog.getId());
        sb.append("\",\"currentProfile\":\"");
        sb.append(currentProfile==null?null:currentProfile.getId());
        sb.append("\",\"deployBehavior\":\"");
        sb.append(deployBehavior==null?null:deployBehavior.toString());
        sb.append("\",\"deployState\":\"");
        sb.append(deployState==null?null:deployState.toString());
        sb.append("\",\"internalIgnoreFilters\":\"");
        sb.append(internalIgnoreFilters==null?null:internalIgnoreFilters.toString());
        sb.append("\",\"locale\":\"");
        sb.append(locale==null?null:locale.getLocaleCode());
        sb.append("\",\"validateProductionChangesState\":\"");
        sb.append(validateProductionChangesState==null?null:validateProductionChangesState.toString());
        sb.append("\",\"timeZone\":\"");
        sb.append(timeZone==null?null:timeZone.getID());
        sb.append("\"}");
        return sb.toString();
    }

    /**
     * Resurrect the BroadleafRequestContext state based on a JSON representation.
     *
     * @param Json
     * @param em
     * @return
     */
    public static BroadleafRequestContext createLightWeightCloneFromJson(String Json, EntityManager em) {
        BroadleafRequestContext context = new BroadleafRequestContext();
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>() {};
        HashMap<String,String> json;
        try {
            json = mapper.readValue(Json, typeRef);
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        }
        if (!json.get("ignoreSite").equals("null")) {
            context.setIgnoreSite(Boolean.valueOf(json.get("ignoreSite")));
        }
        if (!json.get("sandBox").equals("null")) {
            context.setSandBox(em.find(SandBoxImpl.class, Long.parseLong(json.get("sandBox"))));
        }
        if (!json.get("nonPersistentSite").equals("null")) {
            context.setNonPersistentSite(em.find(SiteImpl.class, Long.parseLong(json.get("nonPersistentSite"))));
        }
        if (!json.get("enforceEnterpriseCollectionBehaviorState").equals("null")) {
            context.setEnforceEnterpriseCollectionBehaviorState(EnforceEnterpriseCollectionBehaviorState.valueOf(json
                    .get("enforceEnterpriseCollectionBehaviorState")));
        }
        if (!json.get("admin").equals("null")) {
            context.setAdmin(Boolean.valueOf(json.get("admin")));
        }
        if (!json.get("adminUserId").equals("null")) {
            context.setAdminUserId(Long.parseLong(json.get("ignoreSite")));
        }
        if (!json.get("broadleafCurrency").equals("null")) {
            context.setBroadleafCurrency(em.find(BroadleafCurrencyImpl.class, json.get("broadleafCurrency")));
        }
        if (!json.get("currentCatalog").equals("null")) {
            context.setCurrentCatalog(em.find(CatalogImpl.class, Long.parseLong(json.get("currentCatalog"))));
        }
        if (!json.get("currentProfile").equals("null")) {
            context.setCurrentProfile(em.find(SiteImpl.class, Long.parseLong(json.get("currentProfile"))));
        }
        if (!json.get("deployBehavior").equals("null")) {
            context.setDeployBehavior(DeployBehavior.valueOf(json.get("deployBehavior")));
        }
        if (!json.get("deployState").equals("null")) {
            context.setDeployState(DeployState.valueOf(json.get("deployState")));
        }
        if (!json.get("internalIgnoreFilters").equals("null")) {
            context.setInternalIgnoreFilters(Boolean.valueOf(json.get("internalIgnoreFilters")));
        }
        if (!json.get("locale").equals("null")) {
            context.setLocale(em.find(LocaleImpl.class, json.get("locale")));
        }
        if (!json.get("validateProductionChangesState").equals("null")) {
            context.setValidateProductionChangesState(ValidateProductionChangesState.valueOf(json.get("validateProductionChangesState")));
        }
        if (!json.get("timeZone").equals("null")) {
            context.setTimeZone(TimeZone.getTimeZone(json.get("timeZone")));
        }

        return context;
    }
}
