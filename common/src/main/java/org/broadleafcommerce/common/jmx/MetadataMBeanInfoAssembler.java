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

package org.broadleafcommerce.common.jmx;

import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

/**
 * The MetadataMBeanInfoAssembler provided by Spring does not allow the usage of JDK dynamic proxies. However, several
 * of our services are AOP proxied for the sake of transactions, and the default behavior is to use JDK dynamic proxies for this.
 * It is possible to cause Spring to use CGLIB proxies instead via configuration, but this causes problems when it is desireable
 * or necessary to use constructor injection for the service definition, since CGLIB proxies require a default, no argument
 * constructor.
 * 
 * This class enhances the behavior of the Spring implementation to retrieve the rootId object inside the proxy for the sake of
 * metadata retrieval, thereby working around these shortcomings.
 * 
 * @author jfischer
 *
 */
public class MetadataMBeanInfoAssembler extends org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler {

    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
        //do nothing
    }

    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        managedBean = AspectUtil.exposeRootBean(managedBean);
        return super.getNotificationInfo(managedBean, beanKey);
    }

    protected void populateMBeanDescriptor(Descriptor desc, Object managedBean, String beanKey) {
        managedBean = AspectUtil.exposeRootBean(managedBean);
        super.populateMBeanDescriptor(desc, managedBean, beanKey);
    }

    protected ModelMBeanAttributeInfo[] getAttributeInfo(Object managedBean, String beanKey) throws JMException {
        managedBean = AspectUtil.exposeRootBean(managedBean);
        return super.getAttributeInfo(managedBean, beanKey);
    }

    protected ModelMBeanOperationInfo[] getOperationInfo(Object managedBean, String beanKey) {
        managedBean = AspectUtil.exposeRootBean(managedBean);
        return super.getOperationInfo(managedBean, beanKey);
    }
}
