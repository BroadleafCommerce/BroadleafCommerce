/*
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.gwtwidgets.server.spring.util;

/**
 * For RnD. Records RPC request & responses which are replayed in the unit tests.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */

import java.io.FileOutputStream;

import org.gwtwidgets.server.spring.GWTRPCServiceExporter;

public class TracingExporter extends GWTRPCServiceExporter{

	public static int responseCounter = 0;
	public static int requestCounter = 0;

	public static void dump(String filename, String content) {
		try {
			FileOutputStream fos = new FileOutputStream("C:/Users/george/Documents/logs/"+filename);
			fos.write(content.getBytes("UTF-8"));
			fos.flush();
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onAfterResponseSerialized(String serializedResponse) {
		responseCounter++;
		dump("response."+responseCounter+".txt", serializedResponse);
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC response: [" + serializedResponse + "]");
	}

	@Override
	protected void onBeforeRequestDeserialized(String serializedRequest) {
		requestCounter++;
		dump("request."+requestCounter+".txt", serializedRequest);
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC request: [" + serializedRequest + "]");
	}

}
