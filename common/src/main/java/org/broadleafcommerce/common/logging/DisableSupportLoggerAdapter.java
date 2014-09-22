/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.logging;

/**
 * <p>An implementation of SupportLoggerAdapter that would disable SupportLogger logging. (i.e. do nothing)</p>
 * @author Elbert Bautista (elbertbautista)
 */
public class DisableSupportLoggerAdapter implements SupportLoggerAdapter {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
        //do nothing
    }

    @Override
    public void support(String message) {
        //do nothing
    }

    @Override
    public void support(String message, Throwable t) {
        //do nothing
    }

    @Override
    public void lifecycle(LifeCycleEvent lifeCycleEvent, String message) {
        //do nothing
    }

    @Override
    public void debug(String message) {
        //do nothing
    }

    @Override
    public void debug(String message, Throwable t) {
        //do nothing
    }

    @Override
    public void error(String message) {
        //do nothing
    }

    @Override
    public void error(String message, Throwable t) {
        //do nothing
    }

    @Override
    public void fatal(String message) {
        //do nothing
    }

    @Override
    public void fatal(String message, Throwable t) {
        //do nothing
    }

    @Override
    public void info(String message) {
        //do nothing
    }

    @Override
    public void info(String message, Throwable t) {
        //do nothing
    }

    @Override
    public void warn(String message) {
        //do nothing
    }

    @Override
    public void warn(String message, Throwable t) {
        //do nothing
    }

}
