/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring.gilead;

import net.sf.gilead.core.PersistentBeanManager;

import org.gwtwidgets.server.spring.RPCServiceExporter;
import org.gwtwidgets.server.spring.RPCServiceExporterFactory;

/**
 * Factory which produces {@link GileadRPCServiceExporter} instances.
 * Has to be setup with a {@link PersistentBeanManager}. 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
public abstract class GileadRPCServiceExporterFactory implements RPCServiceExporterFactory{

	public abstract RPCServiceExporter create();

}
