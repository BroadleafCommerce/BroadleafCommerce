/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.client.view.pages;

import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PagesDisplay extends DynamicEditDisplay {

    public DynamicEntityListDisplay getListDisplay();

    public DynamicFormDisplay getDynamicFormDisplay();

    public ComboBoxItem getCurrentLocale();

    public void setCurrentLocale(ComboBoxItem currentLocale);

}
