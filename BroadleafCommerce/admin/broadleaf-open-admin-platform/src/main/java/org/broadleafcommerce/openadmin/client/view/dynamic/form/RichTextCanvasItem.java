package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.smartgwt.client.widgets.form.fields.CanvasItem;

public class RichTextCanvasItem extends CanvasItem {
    
	@Override
	public Object getValue() {
		return ((RichTextHTMLPane) getCanvas()).getValue();
	}

	@Override
	public void setValue(String value) {
		((RichTextHTMLPane) getCanvas()).setValue(value);
	}

    @Override
    public void setDisabled(Boolean disabled) {
        ((RichTextHTMLPane) getCanvas()).setDisabled(disabled);
    }
}
