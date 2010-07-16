/*
 * XML Type:  ShipDateV3Type
 * Namespace: 
 * Java type: noNamespace.ShipDateV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML ShipDateV3Type(@).
 *
 * This is an atomic type that is a restriction of noNamespace.ShipDateV3Type.
 */
public class ShipDateV3TypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements noNamespace.ShipDateV3Type
{
    private static final long serialVersionUID = 1L;
    
    public ShipDateV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected ShipDateV3TypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName OPTION$0 = 
        new javax.xml.namespace.QName("", "Option");
    
    
    /**
     * Gets the "Option" attribute
     */
    public java.lang.String getOption()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OPTION$0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Option" attribute
     */
    public org.apache.xmlbeans.XmlString xgetOption()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(OPTION$0);
            return target;
        }
    }
    
    /**
     * True if has "Option" attribute
     */
    public boolean isSetOption()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(OPTION$0) != null;
        }
    }
    
    /**
     * Sets the "Option" attribute
     */
    public void setOption(java.lang.String option)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OPTION$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(OPTION$0);
            }
            target.setStringValue(option);
        }
    }
    
    /**
     * Sets (as xml) the "Option" attribute
     */
    public void xsetOption(org.apache.xmlbeans.XmlString option)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(OPTION$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(OPTION$0);
            }
            target.set(option);
        }
    }
    
    /**
     * Unsets the "Option" attribute
     */
    public void unsetOption()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(OPTION$0);
        }
    }
}
