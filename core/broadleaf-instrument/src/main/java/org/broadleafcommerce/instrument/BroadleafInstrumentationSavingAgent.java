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

package org.broadleafcommerce.instrument;

import java.lang.instrument.Instrumentation;

/**
 * Based on org.springframework.instrument.InstrumentationSavingAgent from Spring 3.0.5 by Rod Johnson and Juergen Hoeller
 * 
 * @author jfischer
 *
 */
public class BroadleafInstrumentationSavingAgent {

    private static volatile Instrumentation instrumentation;


    /**
     * Save the {@link Instrumentation} interface exposed by the JVM.
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Starting the Broadleaf instrumentation agent");
        instrumentation = inst;
    }


    /**
     * Return the {@link Instrumentation} interface exposed by the JVM.
     * <p>Note that this agent class will typically not be available in the classpath
     * unless the agent is actually specified on JVM startup. If you intend to do
     * conditional checking with respect to agent availability, consider using
     * {@link org.broadleafcommerce.profile.extensibility.jpa.convert.BroadleafLoadTimeWeaver#getInstrumentation()}
     * instead - which will work without the agent class in the classpath as well.
     * @return the <code>Instrumentation</code> instance previously saved when
     * the {@link #premain} method was called by the JVM; will be <code>null</code>
     * if this class was not used as Java agent when this JVM was started.
     * @see org.broadleafcommerce.profile.extensibility.jpa.convert.BroadleafLoadTimeWeaver#getInstrumentation()
     */
    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
