/*
 * XML Type:  PostageV2Type
 * Namespace: 
 * Java type: noNamespace.PostageV2Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML PostageV2Type(@).
 *
 * This is a complex type.
 */
public class PostageV2TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.PostageV2Type
{
    private static final long serialVersionUID = 1L;
    
    public PostageV2TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MAILSERVICE$0 = 
        new javax.xml.namespace.QName("", "MailService");
    private static final javax.xml.namespace.QName RATE$2 = 
        new javax.xml.namespace.QName("", "Rate");
    
    
    /**
     * Gets the "MailService" element
     */
    public java.lang.String getMailService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MAILSERVICE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "MailService" element
     */
    public org.apache.xmlbeans.XmlString xgetMailService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MAILSERVICE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "MailService" element
     */
    public void setMailService(java.lang.String mailService)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MAILSERVICE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MAILSERVICE$0);
            }
            target.setStringValue(mailService);
        }
    }
    
    /**
     * Sets (as xml) the "MailService" element
     */
    public void xsetMailService(org.apache.xmlbeans.XmlString mailService)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MAILSERVICE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MAILSERVICE$0);
            }
            target.set(mailService);
        }
    }
    
    /**
     * Gets the "Rate" element
     */
    public float getRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RATE$2, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Rate" element
     */
    public org.apache.xmlbeans.XmlFloat xgetRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(RATE$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Rate" element
     */
    public void setRate(float rate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RATE$2);
            }
            target.setFloatValue(rate);
        }
    }
    
    /**
     * Sets (as xml) the "Rate" element
     */
    public void xsetRate(org.apache.xmlbeans.XmlFloat rate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(RATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(RATE$2);
            }
            target.set(rate);
        }
    }
}
