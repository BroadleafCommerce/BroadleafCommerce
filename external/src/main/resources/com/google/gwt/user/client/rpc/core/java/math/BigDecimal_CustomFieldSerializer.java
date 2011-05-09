package com.google.gwt.user.client.rpc.core.java.math;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public class BigDecimal_CustomFieldSerializer {
	
	public static BigDecimal instantiate(SerializationStreamReader streamReader) throws SerializationException {
		return new BigDecimal(streamReader.readString());
	}
	
	public static void serialize(SerializationStreamWriter streamWriter, BigDecimal instance) throws SerializationException {
		streamWriter.writeString(instance.toString());
	}
	
	public static void deserialize(SerializationStreamReader streamReader, BigDecimal instance) {
	}
}
