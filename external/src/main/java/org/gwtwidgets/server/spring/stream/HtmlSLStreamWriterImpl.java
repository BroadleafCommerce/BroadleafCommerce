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
package org.gwtwidgets.server.spring.stream;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.gwtwidgets.client.stream.HtmlStreamReader;

/**
 * <p>Implementation of an {@link SLStreamWriter} writing incremental bits of HTML.
 * The intention is that a browser can access via the DOM these messages and decode them.
 * The GWT counterpart of this writer is the {@link HtmlStreamReader}.
 * </p>
 * <p>The protocol explained in short:<br>
 * A html/body is opened, then each message line consists of a <code>&lt;div&gt;</code>
 * containing the URL-encoded message content and a comma sepperated list of <code>key=value</code>
 * pairs of the message attributes
 * </p>
 * <p>
 * Some browsers do not start interpreting the streamed content unless the stream is either closed
 * or the transmitted content exceeds a certain size. In order to assure a timely consumption of
 * messages on the client side, this implementation pads messages with more data than might be
 * necessary in certain cases.
 * </p>
 * <p>
 * Please note that some networks (i.e. when http proxies are involved) may not stream content
 * prior to closing the stream. Furthermore they may impose stricter constraints on a connection's
 * lifetime than planned. For this reasons applications should be prepared to fall back to shorter
 * timeouts. 
 * </p>
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class HtmlSLStreamWriterImpl implements SLStreamWriter {

	private ServletOutputStream out;
	private boolean preampleSent = false;
	private int serial = 0;
	
	private int getNextSerial(){
		return serial++;
	}

	private void printPreample() {
		if (preampleSent) return;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body>");
			for (int i = 0; i < 1024; i++)
				sb.append(" ");
			out.print(sb.toString());
			out.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		preampleSent = true;
	}
	
	public void setSerialStartpoint(int serial){
		this.serial = serial;
	}
	

	public void close() {
		try {
			out.print("<span>EOF</span></body></html>");
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setResponse(HttpServletResponse response) {
		try {
			this.out = response.getOutputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeMessage(String content, Map<String, ?> attributes) {
		printPreample();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<div id='"+getNextSerial()+"'>");
			sb.append(URLEncoder.encode(content, "UTF-8"));
			if (attributes!=null && attributes.size()>0){
				for (String attribute:attributes.keySet()){
					sb.append(",").append(URLEncoder.encode(attribute, "UTF-8")).append("=").append(URLEncoder.encode(attributes.get(attribute).toString(), "UTF-8"));
				}
			}
			sb.append("</div>");
			out.println(sb.toString());
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
