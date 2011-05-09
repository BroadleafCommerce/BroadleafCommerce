/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Interface for exception handlers of RPC services
 * @author g.georgovassilis[at]gmail.com
 *
 */
public interface ThrowableHandler {

	/**
	 * Is provided with the:
	 * @param throwable that was thrown while invoking on...
	 * @param target the...
	 * @param method with the following...
	 * @param arguments
	 * @return Must return a throwable which can be propagated to the client, i.e. the class
	 * must be known to the client at compile time and implement {@link Serializable} or {@link IsSerializable}
	 */
	Throwable handle(Throwable throwable, Object target, Method method, Object[] arguments);
}
