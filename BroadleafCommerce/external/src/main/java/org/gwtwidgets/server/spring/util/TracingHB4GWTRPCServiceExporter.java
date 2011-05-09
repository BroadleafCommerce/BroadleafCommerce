/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring.util;

import org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter;

/**
 * I'm using this to record RPC messages  
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
public class TracingHB4GWTRPCServiceExporter extends GileadRPCServiceExporter {

	@Override
	protected void onAfterResponseSerialized(String serializedResponse) {
		TracingExporter.responseCounter++;
		TracingExporter.dump("response."+TracingExporter.responseCounter+".txt", serializedResponse);
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC response: [" + serializedResponse + "]");
	}

	@Override
	protected void onBeforeRequestDeserialized(String serializedRequest) {
		TracingExporter.requestCounter++;
		TracingExporter.dump("request."+TracingExporter.requestCounter+".txt", serializedRequest);
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC request: [" + serializedRequest + "]");
	}

}
