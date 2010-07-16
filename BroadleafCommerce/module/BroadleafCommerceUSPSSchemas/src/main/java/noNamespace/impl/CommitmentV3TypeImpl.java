/*
 * XML Type:  CommitmentV3Type
 * Namespace: 
 * Java type: noNamespace.CommitmentV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML CommitmentV3Type(@).
 *
 * This is a complex type.
 */
public class CommitmentV3TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.CommitmentV3Type
{
    private static final long serialVersionUID = 1L;
    
    public CommitmentV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMMITMENTDATE$0 = 
        new javax.xml.namespace.QName("", "CommitmentDate");
    private static final javax.xml.namespace.QName COMMITMENTTIME$2 = 
        new javax.xml.namespace.QName("", "CommitmentTime");
    private static final javax.xml.namespace.QName LOCATION$4 = 
        new javax.xml.namespace.QName("", "Location");
    
    
    /**
     * Gets the "CommitmentDate" element
     */
    public java.lang.String getCommitmentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTDATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CommitmentDate" element
     */
    public org.apache.xmlbeans.XmlString xgetCommitmentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTDATE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "CommitmentDate" element
     */
    public void setCommitmentDate(java.lang.String commitmentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMITMENTDATE$0);
            }
            target.setStringValue(commitmentDate);
        }
    }
    
    /**
     * Sets (as xml) the "CommitmentDate" element
     */
    public void xsetCommitmentDate(org.apache.xmlbeans.XmlString commitmentDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMITMENTDATE$0);
            }
            target.set(commitmentDate);
        }
    }
    
    /**
     * Gets the "CommitmentTime" element
     */
    public java.lang.String getCommitmentTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTTIME$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CommitmentTime" element
     */
    public org.apache.xmlbeans.XmlString xgetCommitmentTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTTIME$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "CommitmentTime" element
     */
    public void setCommitmentTime(java.lang.String commitmentTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTTIME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMITMENTTIME$2);
            }
            target.setStringValue(commitmentTime);
        }
    }
    
    /**
     * Sets (as xml) the "CommitmentTime" element
     */
    public void xsetCommitmentTime(org.apache.xmlbeans.XmlString commitmentTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTTIME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMITMENTTIME$2);
            }
            target.set(commitmentTime);
        }
    }
    
    /**
     * Gets array of all "Location" elements
     */
    public noNamespace.LocationV3Type[] getLocationArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(LOCATION$4, targetList);
            noNamespace.LocationV3Type[] result = new noNamespace.LocationV3Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Location" element
     */
    public noNamespace.LocationV3Type getLocationArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.LocationV3Type target = null;
            target = (noNamespace.LocationV3Type)get_store().find_element_user(LOCATION$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "Location" element
     */
    public int sizeOfLocationArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LOCATION$4);
        }
    }
    
    /**
     * Sets array of all "Location" element
     */
    public void setLocationArray(noNamespace.LocationV3Type[] locationArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(locationArray, LOCATION$4);
        }
    }
    
    /**
     * Sets ith "Location" element
     */
    public void setLocationArray(int i, noNamespace.LocationV3Type location)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.LocationV3Type target = null;
            target = (noNamespace.LocationV3Type)get_store().find_element_user(LOCATION$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(location);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Location" element
     */
    public noNamespace.LocationV3Type insertNewLocation(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.LocationV3Type target = null;
            target = (noNamespace.LocationV3Type)get_store().insert_element_user(LOCATION$4, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Location" element
     */
    public noNamespace.LocationV3Type addNewLocation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.LocationV3Type target = null;
            target = (noNamespace.LocationV3Type)get_store().add_element_user(LOCATION$4);
            return target;
        }
    }
    
    /**
     * Removes the ith "Location" element
     */
    public void removeLocation(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LOCATION$4, i);
        }
    }
}
