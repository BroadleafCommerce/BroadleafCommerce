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

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.form.DynamicForm;
import org.broadleafcommerce.openadmin.client.BLCMain;

import java.util.HashMap;
import java.util.Map;

public class RichTextHTMLPane extends HTMLPane {

    static {
        exposeGetHTMLDisabled();
        exposeGetHTMLValue();
    }

    private static int counter = 0;
    private static Map<String, Map<String, Object>> valueMap = new HashMap<String, Map<String, Object>>();

    private final int myId;
    private String editorPath;
    private DynamicForm form;

    public RichTextHTMLPane(String editorPath, DynamicForm form) {
        super();
        this.editorPath = editorPath;
        counter++;
        myId = counter;
        valueMap.put(String.valueOf(myId), new HashMap<String, Object>());
        valueMap.get(String.valueOf(myId)).put("disabled", false);
        valueMap.get(String.valueOf(myId)).put("form", form);
    }
    
    public void setValue(String value) {
        valueMap.get(String.valueOf(myId)).put("value", value);
        init();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (valueMap.size() > 0) {
            valueMap.clear();
        }
    }

    public String getValue()  {
        Node node = findIFrame();
        String response = findRichTextValue(node);
        setValue(response);

        return response;
    }
    
    public Node findIFrame() {
        Element element = getDOM();
        Node iframe =  findIFrame(element.getChildNodes());
        return iframe;
    }
    
    public Node findIFrame(NodeList<Node> childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.getItem(i);
            if (item instanceof Element && "IFRAME".equals(((Element) item).getTagName())) {
                return item;
            } else {
                Node childIFrame = findIFrame(item.getChildNodes());
                if (childIFrame != null) {
                    return childIFrame;
                }
            }
        }
        return null;
    }

    public void init() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(myId));
        setContentsURLParams(map);
        if (editorPath.startsWith("http")) {
            setContentsURL(editorPath);
        } else {
            setContentsURL(BLCMain.webAppContext + editorPath);
        }
    }

    public void setDisabled(Boolean disabled) {
        if (!valueMap.get(String.valueOf(myId)).get("disabled").equals(disabled.toString())) {
            valueMap.get(String.valueOf(myId)).put("disabled", disabled);
            init();
        }
    }

    public static String getHTMLValue(String id) {
        String response = (String) valueMap.get(id).get("value");
        return response==null?"":response;
    }

    public static boolean getHTMLDisabled(String id) {
        if (!((DynamicForm) valueMap.get(id).get("form")).getDisabled()) {
            return ((Boolean) valueMap.get(id).get("disabled"));
        }
        return true;
    }
   
    public static native String findRichTextValue(Node iframeNode) /*-{
        return iframeNode.contentWindow.tinyMCE.get('richTextContent').getContent();
    }-*/;

    private static native void exposeGetHTMLDisabled() /*-{
        $wnd.getHTMLDisabled = function(id) {
            return @org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextHTMLPane::getHTMLDisabled(Ljava/lang/String;)(id);
        }
    }-*/;

    private static native void exposeGetHTMLValue() /*-{
        $wnd.getHTMLValue = function(id) {
            return @org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextHTMLPane::getHTMLValue(Ljava/lang/String;)(id);
        }
    }-*/;
}
