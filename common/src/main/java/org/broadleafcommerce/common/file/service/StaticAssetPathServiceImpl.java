/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.file.service;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.view.ImportSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("blStaticAssetPathService")
public class StaticAssetPathServiceImpl implements StaticAssetPathService {

    @Value("${asset.server.url.prefix.internal}")
    protected String staticAssetUrlPrefix;

    @Value("${asset.server.url.prefix}")
    protected String staticAssetEnvironmentUrlPrefix;

    @Value("${asset.server.url.prefix.secure}")
    protected String staticAssetEnvironmentSecureUrlPrefix;

    /**
     * This method will take in a content string (e.g. StructuredContentDTO or PageDTO HTML/ASSET_LOOKUP/STRING field value)
     * and replace any instances of "staticAssetUrlPrefix" in the string with the "staticAssetEnvironmentUrlPrefix"
     * or the "staticAssetEnvironmentSecureUrlPrefix" depending on if the request was secure and if it was configured.
     *
     * Given asset.server.url.prefix.internal=cmsstatic
     * Given asset.server.url.prefix=http://static.mydomain.com/cmsstatic
     * Given asset.server.url.prefix.secure=https://static.mydomain.com/cmsstatic
     *
     * Example 1:
     * Given content = "<p><img src="/cmsstatic/my_image.jpg"/></p>"
     *
     * The result should yield: "<p><img src="http://static.mydomain.com/cmsstatic/my_image.jpg"/></p>"
     *
     * Example 2:
     * Given content = "<p><img src="cmsstatic/my_image_2.jpg"/></p>"
     *
     * The result should yield: "<p><img src="http://static.mydomain.com/cmsstatic/my_image_2.jpg"/></p>"
     *
     * @param content       - The content string to rewrite if it contains a cms managed asset
     * @param secureRequest - True if the request is being served over https
     * @return
     * @see org.broadleafcommerce.common.file.service.StaticAssetService#getStaticAssetUrlPrefix()
     * @see org.broadleafcommerce.common.file.service.StaticAssetService#getStaticAssetEnvironmentUrlPrefix()
     */
    @Override
    public String convertAllAssetPathsInContent(String content, boolean secureRequest) {
        String returnValue = content;

        if (StringUtils.isNotBlank(content) &&
                StringUtils.isNotBlank(getStaticAssetUrlPrefix()) &&
                StringUtils.isNotBlank(getStaticAssetEnvironmentUrlPrefix()) &&
                content.contains(getStaticAssetUrlPrefix())) {

            final String envPrefix;
            if (secureRequest) {
                envPrefix = getStaticAssetEnvironmentSecureUrlPrefix();
            } else {
                envPrefix = getStaticAssetEnvironmentUrlPrefix();
            }

            if (envPrefix != null) {
                String trailing = "";
                if (envPrefix.endsWith("/")) {
                    trailing = "/";
                }
                returnValue = returnValue.replaceAll(getStaticAssetUrlPrefix()+trailing, envPrefix);
                //Catch any scenario where there is a leading "/" after the replacement
                returnValue = returnValue.replaceAll("/"+envPrefix, envPrefix);
            }

        }

        return returnValue;
    }

    /**
     * This method will take in an assetPath (think image url) and prepend the
     * staticAssetUrlPrefix if one exists.
     * 
     * Will append any contextPath onto the request.    If the incoming assetPath contains
     * the internalStaticAssetPrefix and the image is being prepended, the prepend will be
     * removed.
     *
     * Example 1:
     * Given asset.server.url.prefix.internal=cmsstatic
     * Given asset.server.url.prefix=http://static.mydomain.com/cmsstatic
     * Given asset.server.url.prefix.secure=https://static.mydomain.com/cmsstatic
     * Given assetPath = "/cmsstatic/my_image.jpg"
     *
     * The result should yield: "http://static.mydomain.com/cmsstatic/my_image.jpg"
     *
     * Example 2:
     * Given asset.server.url.prefix.internal=cmsstatic
     * Given asset.server.url.prefix=
     * Given asset.server.url.prefix.secure=
     * Given assetPath = "/cmsstatic/my_image.jpg"
     * Given contextPath = "myApp"
     *
     * The result should yield: "/myApp/cmsstatic/my_image.jpg"
     *
     * @param assetPath     - The path to rewrite if it is a cms managed asset
     * @param contextPath   - The context path of the web application (if applicable)
     * @param secureRequest - True if the request is being served over https
     * @return
     * @see org.broadleafcommerce.common.file.service.StaticAssetService#getStaticAssetUrlPrefix()
     * @see org.broadleafcommerce.common.file.service.StaticAssetService#getStaticAssetEnvironmentUrlPrefix()
     */
    @Override
    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest) {
        String returnValue = assetPath;
        
        if (assetPath != null && getStaticAssetEnvironmentUrlPrefix() != null && ! "".equals(getStaticAssetEnvironmentUrlPrefix())) {
            final String envPrefix;
            if (secureRequest) {
                envPrefix = getStaticAssetEnvironmentSecureUrlPrefix();
            } else {
                envPrefix = getStaticAssetEnvironmentUrlPrefix();
            }
            if (envPrefix != null) {
                // remove the starting "/" if it exists.
                if (returnValue.startsWith("/")) {
                    returnValue = returnValue.substring(1);
                }

                // Also, remove the "cmsstatic" from the URL before prepending the staticAssetUrlPrefix.
                if (returnValue.startsWith(getStaticAssetUrlPrefix())) {
                    returnValue = returnValue.substring(getStaticAssetUrlPrefix().trim().length());

                    // remove the starting "/" if it exists.
                    if (returnValue.startsWith("/")) {
                        returnValue = returnValue.substring(1);
                    }
                }                
                returnValue = envPrefix + returnValue;
            }
        } else {
            if (returnValue != null && ! ImportSupport.isAbsoluteUrl(returnValue)) {
                if (! returnValue.startsWith("/")) {
                    returnValue = "/" + returnValue;
                }

                // Add context path
                if (contextPath != null && ! contextPath.equals("")) {
                    if (! contextPath.equals("/")) {
                        // Shouldn't be the case, but let's handle it anyway
                        if (contextPath.endsWith("/")) {
                            returnValue = returnValue.substring(1);
                        }
                        if (contextPath.startsWith("/")) {
                            returnValue = contextPath + returnValue;  // normal case
                        } else {
                            returnValue = "/" + contextPath + returnValue;
                        }
                    }
                }
            }
        }

        return returnValue;
    }

    @Override
    public String getStaticAssetUrlPrefix() {
        return staticAssetUrlPrefix;
    }

    @Override
    public void setStaticAssetUrlPrefix(String staticAssetUrlPrefix) {
        this.staticAssetUrlPrefix = staticAssetUrlPrefix;
    }

    @Override
    public String getStaticAssetEnvironmentUrlPrefix() {
        return fixEnvironmentUrlPrefix(staticAssetEnvironmentUrlPrefix);
    }

    @Override
    public void setStaticAssetEnvironmentUrlPrefix(String staticAssetEnvironmentUrlPrefix) {
        this.staticAssetEnvironmentUrlPrefix = staticAssetEnvironmentUrlPrefix;
    }

    @Override
    public String getStaticAssetEnvironmentSecureUrlPrefix() {
        if (StringUtils.isEmpty(staticAssetEnvironmentSecureUrlPrefix)) {
            if (!StringUtils.isEmpty(staticAssetEnvironmentUrlPrefix) && staticAssetEnvironmentUrlPrefix.indexOf("http:") >= 0) {
                staticAssetEnvironmentSecureUrlPrefix = staticAssetEnvironmentUrlPrefix.replace("http:", "https:");
            }
        }
        return fixEnvironmentUrlPrefix(staticAssetEnvironmentSecureUrlPrefix);
    }

    public void setStaticAssetEnvironmentSecureUrlPrefix(String staticAssetEnvironmentSecureUrlPrefix) {        
        this.staticAssetEnvironmentSecureUrlPrefix = staticAssetEnvironmentSecureUrlPrefix;
    }

    /**
     * Trims whitespace.   If the value is the same as the internal url prefix, then return
     * null.
     *
     * @param urlPrefix
     * @return
     */
    private String fixEnvironmentUrlPrefix(String urlPrefix) {
        if (urlPrefix != null) {
            urlPrefix = urlPrefix.trim();
            if ("".equals(urlPrefix)) {
                // The value was not set.
                urlPrefix = null;
            } else if (urlPrefix.equals(staticAssetUrlPrefix)) {
                // The value is the same as the default, so no processing needed.
                urlPrefix = null;
            }
        }

        if (urlPrefix != null && !urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        return urlPrefix;
    }

}
