/*
 * XML Type:  RateV3RequestType
 * Namespace: 
 * Java type: noNamespace.RateV3RequestType
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML RateV3RequestType(@).
 *
 * This is a complex type.
 */
public class RateV3RequestTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RateV3RequestType
{
    private static final long serialVersionUID = 1L;
    
    public RateV3RequestTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PACKAGE$0 = 
        new javax.xml.namespace.QName("", "Package");
    private static final javax.xml.namespace.QName USERID$2 = 
        new javax.xml.namespace.QName("", "USERID");
    private static final javax.xml.namespace.QName PASSWORD$4 = 
        new javax.xml.namespace.QName("", "PASSWORD");
    
    
    /**
     * Gets array of all "Package" elements
     */
    public noNamespace.RequestPackageV3Type[] getPackageArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(PACKAGE$0, targetList);
            noNamespace.RequestPackageV3Type[] result = new noNamespace.RequestPackageV3Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Package" element
     */
    public noNamespace.RequestPackageV3Type getPackageArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type target = null;
            target = (noNamespace.RequestPackageV3Type)get_store().find_element_user(PACKAGE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "Package" element
     */
    public int sizeOfPackageArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PACKAGE$0);
        }
    }
    
    /**
     * Sets array of all "Package" element
     */
    public void setPackageArray(noNamespace.RequestPackageV3Type[] xpackageArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(xpackageArray, PACKAGE$0);
        }
    }
    
    /**
     * Sets ith "Package" element
     */
    public void setPackageArray(int i, noNamespace.RequestPackageV3Type xpackage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type target = null;
            target = (noNamespace.RequestPackageV3Type)get_store().find_element_user(PACKAGE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(xpackage);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Package" element
     */
    public noNamespace.RequestPackageV3Type insertNewPackage(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type target = null;
            target = (noNamespace.RequestPackageV3Type)get_store().insert_element_user(PACKAGE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Package" element
     */
    public noNamespace.RequestPackageV3Type addNewPackage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type target = null;
            target = (noNamespace.RequestPackageV3Type)get_store().add_element_user(PACKAGE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "Package" element
     */
    public void removePackage(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PACKAGE$0, i);
        }
    }
    
    /**
     * Gets the "USERID" attribute
     */
    public java.lang.String getUSERID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(USERID$2);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "USERID" attribute
     */
    public org.apache.xmlbeans.XmlString xgetUSERID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(USERID$2);
            return target;
        }
    }
    
    /**
     * Sets the "USERID" attribute
     */
    public void setUSERID(java.lang.String userid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(USERID$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(USERID$2);
            }
            target.setStringValue(userid);
        }
    }
    
    /**
     * Sets (as xml) the "USERID" attribute
     */
    public void xsetUSERID(org.apache.xmlbeans.XmlString userid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(USERID$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(USERID$2);
            }
            target.set(userid);
        }
    }
    
    /**
     * Gets the "PASSWORD" attribute
     */
    public java.lang.String getPASSWORD()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PASSWORD$4);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "PASSWORD" attribute
     */
    public org.apache.xmlbeans.XmlString xgetPASSWORD()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PASSWORD$4);
            return target;
        }
    }
    
    /**
     * True if has "PASSWORD" attribute
     */
    public boolean isSetPASSWORD()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PASSWORD$4) != null;
        }
    }
    
    /**
     * Sets the "PASSWORD" attribute
     */
    public void setPASSWORD(java.lang.String password)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PASSWORD$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PASSWORD$4);
            }
            target.setStringValue(password);
        }
    }
    
    /**
     * Sets (as xml) the "PASSWORD" attribute
     */
    public void xsetPASSWORD(org.apache.xmlbeans.XmlString password)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PASSWORD$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PASSWORD$4);
            }
            target.set(password);
        }
    }
    
    /**
     * Unsets the "PASSWORD" attribute
     */
    public void unsetPASSWORD()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PASSWORD$4);
        }
    }
}
