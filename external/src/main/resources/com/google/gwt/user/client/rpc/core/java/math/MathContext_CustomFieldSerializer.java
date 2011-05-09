package com.google.gwt.user.client.rpc.core.java.math;

import java.math.MathContext;
import java.math.RoundingMode;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public class MathContext_CustomFieldSerializer {
	
	public static MathContext instantiate(SerializationStreamReader streamReader) throws SerializationException {
		return new MathContext(streamReader.readInt(), RoundingMode.valueOf(streamReader.readInt()));
	}
	
	public static void serialize(SerializationStreamWriter streamWriter, MathContext instance) throws SerializationException {
		streamWriter.writeInt(instance.getPrecision());
		streamWriter.writeInt(instance.getRoundingMode().ordinal());
	}
	
	public static void deserialize(SerializationStreamReader streamReader, MathContext instance) {
	}
}
