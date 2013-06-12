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

package org.broadleafcommerce.common.web.resource;

/**
 * Service responsible for interacting with YUI-compressor to minify JS/CSS resources.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface ResourceMinificationService {

    /**
     * Given the source byte[], will return a byte[] that represents the YUI-compressor minified version
     * of the byte[]. The behavior of this method is controlled via the following properties:
     * 
     * <ul>
     *  <li>minify.enabled - whether or not to actually perform minification</li>
     *  <li>minify.linebreak - if set to a value other than -1, will enforce a linebreak at that value</li>
     *  <li>minify.munge - if true, will replace variable names with shorter versions</li>
     *  <li>minify.verbose - if true, will display extra logging information to the console</li>
     *  <li>minify.preserveAllSemiColons - if true, will never remove semi-colons, even if two in a row exist</li>
     *  <li>minify.disableOptimizations - if true, will disable some micro-optimizations that are performed</li>
     * </ul>
     * 
     * @param filename
     * @param bytes
     * @return the minified bytes
     */
    public byte[] minify(String filename, byte[] bytes);

}