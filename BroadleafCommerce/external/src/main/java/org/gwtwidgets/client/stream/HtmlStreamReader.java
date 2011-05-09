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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;

/**
 * Decodes SL stream protocol messages sent via the
 * {@link HtmlSLStreamWriterImpl}. This implementation creates an invisible
 * IFrame which is appended to the current document and polled in intervals.
 * Decoded messages are then removed from the DOM in order to reduce resource
 * consumption.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */

public class HtmlStreamReader {

	protected IFrameElement frame;
	protected long pollingInterval = 1000;
	protected long maxAge = 5 * 60*1000;
	protected Timer timer;
	protected Date dateOfCreation;
	protected int lastMessageId = -1;

	private boolean isObsolete() {
		Date now = new Date();
		long diff = now.getTime() - dateOfCreation.getTime();
		return diff > maxAge;
	}

	//workaround until GWT #2805 is released
	protected native Document getFrameContents(IFrameElement frame)/*-{
			return frame.contentWindow.document; 
		}-*/;

	private Message decodeMessage(Element e) {
		Message message = new Message();
		String content = e.getInnerText();
		String[] parts = content.split(",");
		message.setContent(URL.decodeComponent(parts[0]));
		for (int i = 1; i < parts.length; i++) {
			String[] keyValuePair = parts[i].split("=");
			message.getAttributes().put(URL.decodeComponent(keyValuePair[0]), URL.decodeComponent(keyValuePair[1]));
		}
		message.setSerialNumber(Integer.parseInt(e.getAttribute("id")));
		return message;
	}

	private Message checkForEOF(Document document) {
		NodeList<Element> eSpans = document.getElementsByTagName("span");
		if (eSpans.getLength() == 0)
			return null;
		Element eSpan = eSpans.getItem(0);
		if ("EOF".equals(eSpan.getInnerText())) {
			Message message = new Message();
			message.setEOF(true);
			return message;
		}
		return null;
	}

	protected Message readNextMessage() {
		Document document = getFrameContents(frame);
		if (null == document)
			return null;
		NodeList<Element> elements = document.getElementsByTagName("div");
		if (elements.getLength() == 0) {
			return checkForEOF(document);
		}
		Element eDiv = elements.getItem(0);
		eDiv.getParentElement().removeChild(eDiv);
		Message message = decodeMessage(eDiv);
		lastMessageId = message.getSerialNumber();
		return message;
	};

	public long getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Sets a polling interval - messages arriving from the server will be
	 * decoded every such interval. Please note that this parameter is passed to
	 * {@link Timer#schedule(int)} which receives an integer argument; hence
	 * large values for <code>pollingInterval</code> will not yield the
	 * desired behavior.
	 * 
	 * @param pollingInterval
	 *            Polling interval in milliseconds
	 */
	public void setPollingInterval(long pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	protected Element getFrame() {
		return frame;
	}

	/**
	 * For debugging purposes, can be used to show the IFrame used to receive
	 * server messages.
	 * 
	 * @param status
	 */
	public void setInnerFrameVisibility(boolean status) {
		//		frame.getStyle().setProperty("visibility", status?"visible":"hidden");
	}

	public HtmlStreamReader() {
	}

	/**
	 * Connects to a URL an starts streaming messages from there. Note that
	 * despite this being an asynchronous message, the {@link HtmlStreamReader}
	 * is a stateful object which cannot be used to simultaneously access more
	 * resources.
	 * 
	 * @param url
	 * @param callback
	 */
	public void readAsync(final String url, final StreamCallback callback) {
		GWT.log("Openeing stream to "+url, null);
		final String sUrl = url+(url.contains("&")?"&":"?")+"lastmessageid="+lastMessageId;
		dateOfCreation = new Date();
		frame = Document.get().createIFrameElement();
		setInnerFrameVisibility(false);
		Document.get().getBody().appendChild(frame);
		frame.setSrc(sUrl);
		timer = new Timer() {
			@Override
			public void run() {
				try {
					Message message = null;
					while (null != (message = readNextMessage())) {
						if (message.isEOF()) {
							close();
							callback.onDisconnect();
							return;
						}
						callback.onMessage(message);
					}
					if (isObsolete()) {
						reconnect(url, callback);
						return;
					}
					timer.schedule((int) getPollingInterval());
				} catch (Exception e) {
					close();
					callback.onError(e);
				}
			}
		};
		timer.schedule((int) getPollingInterval());
	}
	
	protected void reconnect(String url, StreamCallback callback){
		GWT.log("Refreshing connection to server with id "+lastMessageId, null);
		close();
		readAsync(url, callback);
	}

	/**
	 * Should be called if the client wants to close the stream communication
	 * with the server.
	 */
	public void close() {
		frame.setSrc("");
		timer.cancel();
		frame.getParentElement().removeChild(frame);
		frame = null;
	}

	/**
	 * Determines the age (in ms) of a connection to a server before closing and refreshing it
	 * @param maxAge
	 */
	public void setMaxAge(long maxAge) {
		this.maxAge = maxAge;
	}
}
