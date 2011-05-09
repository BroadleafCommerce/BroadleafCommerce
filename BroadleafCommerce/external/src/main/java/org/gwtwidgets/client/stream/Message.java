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
package org.gwtwidgets.client.stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a message sent over the streaming protocol
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */

public class Message {

	private String content;
	private Map<String, String> attributes = new HashMap<String, String>();
	private boolean EOF = false;
	private int serialNumber;

	/**
	 * True if this is the last message in the stream
	 * @return
	 */
	public boolean isEOF() {
		return EOF;
	}

	public void setEOF(boolean eof) {
		EOF = eof;
	}

	/**
	 * Message content. Can be any valid string.
	 * @return
	 */
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * Returns reference to the message attribute map. Message attributes can be any
	 * set of key/value pairs.
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		String s = "Message #"+getSerialNumber()+" content:["+(content !=null?content:"")+"]";
		if (attributes!=null)
			for (String key:attributes.keySet()){
				s+=","+key+"="+attributes.get(key);
			}
		return s;
	}
}
