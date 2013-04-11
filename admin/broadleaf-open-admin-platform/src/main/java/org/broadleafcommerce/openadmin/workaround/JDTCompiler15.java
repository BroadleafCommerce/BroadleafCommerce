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

package org.broadleafcommerce.openadmin.workaround;

import org.apache.tools.ant.taskdefs.Javac;
import org.eclipse.jdt.core.JDTCompilerAdapter;
@Deprecated
public class JDTCompiler15 extends JDTCompilerAdapter {

    @Override
    public void setJavac(Javac attributes) {
        if (attributes.getTarget() == null) {
              attributes.setTarget("1.6");
            }
            if (attributes.getSource() == null) {
              attributes.setSource("1.6");
            }
            super.setJavac(attributes);
    }

}
