/*
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.gwtwidgets.server.spring.stream;

import javax.servlet.http.HttpServletRequest;

import org.gwtwidgets.client.stream.Message;

/**
 * Backend message producer. Generated messages will be streamed to the client. 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 *
 */
public interface MessageProvider {

	Message getMessage(int id, HttpServletRequest request);
}
