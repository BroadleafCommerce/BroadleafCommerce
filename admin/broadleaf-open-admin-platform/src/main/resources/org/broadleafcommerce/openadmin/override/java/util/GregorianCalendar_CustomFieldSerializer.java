/*
 * Copyright 2008-2012 the original author or authors.
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
package java.util;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

/**
 * Custom field serializer classes allow customization of the serialization
 * process used by GWT. For example, GWT has an out-of-the-box requirement
 * that classes used in RPC have zero argument constructors. However, using
 * a custom serializer (as below) can allow the usage of a class that has
 * one or more constructor argument requirements.
 * 
 * @author jfischer
 * 
 */
public final class GregorianCalendar_CustomFieldSerializer {

	public static void deserialize(SerializationStreamReader streamReader, GregorianCalendar instance)
    throws SerializationException {
		//do nothing
	}

	public static void serialize(SerializationStreamWriter streamWriter, GregorianCalendar instance)
    throws SerializationException {
		streamWriter.writeInt(instance.get(Calendar.YEAR));
		streamWriter.writeInt(instance.get(Calendar.MONTH));
		streamWriter.writeInt(instance.get(Calendar.DATE));
		streamWriter.writeInt(instance.get(Calendar.HOUR));
		streamWriter.writeInt(instance.get(Calendar.MINUTE));
		streamWriter.writeInt(instance.get(Calendar.SECOND));
	}
	
	public static GregorianCalendar instantiate(SerializationStreamReader streamReader)
    throws SerializationException {
	    return new GregorianCalendar(streamReader.readInt(),streamReader.readInt(),streamReader.readInt(),streamReader.readInt(),streamReader.readInt(),streamReader.readInt());
	}
}
