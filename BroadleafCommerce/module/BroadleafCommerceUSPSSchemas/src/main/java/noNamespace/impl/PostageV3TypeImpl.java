/*
 * XML Type:  PostageV3Type
 * Namespace: 
 * Java type: noNamespace.PostageV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML PostageV3Type(@).
 *
 * This is a complex type.
 */
public class PostageV3TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.PostageV3Type
{
    private static final long serialVersionUID = 1L;
    
    public PostageV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MAILSERVICE$0 = 
        new javax.xml.namespace.QName("", "MailService");
    private static final javax.xml.namespace.QName RATE$2 = 
        new javax.xml.namespace.QName("", "Rate");
    private static final javax.xml.namespace.QName COMMERCIALRATE$4 = 
        new javax.xml.namespace.QName("", "CommercialRate");
    private static final javax.xml.namespace.QName COMMITMENTDATE$6 = 
        new javax.xml.namespace.QName("", "CommitmentDate");
    private static final javax.xml.namespace.QName LOCATION$8 = 
        new javax.xml.namespace.QName("", "Location");
    private static final javax.xml.namespace.QName COMMITMENT$10 = 
        new javax.xml.namespace.QName("", "Commitment");
    private static final javax.xml.namespace.QName CLASSID$12 = 
        new javax.xml.namespace.QName("", "CLASSID");
    
    
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
    
    /**
     * Gets the "CommercialRate" element
     */
    public float getCommercialRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMERCIALRATE$4, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "CommercialRate" element
     */
    public org.apache.xmlbeans.XmlFloat xgetCommercialRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(COMMERCIALRATE$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "CommercialRate" element
     */
    public boolean isSetCommercialRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COMMERCIALRATE$4) != 0;
        }
    }
    
    /**
     * Sets the "CommercialRate" element
     */
    public void setCommercialRate(float commercialRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMERCIALRATE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMERCIALRATE$4);
            }
            target.setFloatValue(commercialRate);
        }
    }
    
    /**
     * Sets (as xml) the "CommercialRate" element
     */
    public void xsetCommercialRate(org.apache.xmlbeans.XmlFloat commercialRate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(COMMERCIALRATE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(COMMERCIALRATE$4);
            }
            target.set(commercialRate);
        }
    }
    
    /**
     * Unsets the "CommercialRate" element
     */
    public void unsetCommercialRate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COMMERCIALRATE$4, 0);
        }
    }
    
    /**
     * Gets the "CommitmentDate" element
     */
    public java.lang.String getCommitmentDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTDATE$6, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTDATE$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMITMENTDATE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMITMENTDATE$6);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMITMENTDATE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMITMENTDATE$6);
            }
            target.set(commitmentDate);
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
            get_store().find_all_element_users(LOCATION$8, targetList);
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
            target = (noNamespace.LocationV3Type)get_store().find_element_user(LOCATION$8, i);
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
            return get_store().count_elements(LOCATION$8);
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
            arraySetterHelper(locationArray, LOCATION$8);
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
            target = (noNamespace.LocationV3Type)get_store().find_element_user(LOCATION$8, i);
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
            target = (noNamespace.LocationV3Type)get_store().insert_element_user(LOCATION$8, i);
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
            target = (noNamespace.LocationV3Type)get_store().add_element_user(LOCATION$8);
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
            get_store().remove_element(LOCATION$8, i);
        }
    }
    
    /**
     * Gets array of all "Commitment" elements
     */
    public noNamespace.CommitmentV3Type[] getCommitmentArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(COMMITMENT$10, targetList);
            noNamespace.CommitmentV3Type[] result = new noNamespace.CommitmentV3Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Commitment" element
     */
    public noNamespace.CommitmentV3Type getCommitmentArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.CommitmentV3Type target = null;
            target = (noNamespace.CommitmentV3Type)get_store().find_element_user(COMMITMENT$10, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "Commitment" element
     */
    public int sizeOfCommitmentArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COMMITMENT$10);
        }
    }
    
    /**
     * Sets array of all "Commitment" element
     */
    public void setCommitmentArray(noNamespace.CommitmentV3Type[] commitmentArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(commitmentArray, COMMITMENT$10);
        }
    }
    
    /**
     * Sets ith "Commitment" element
     */
    public void setCommitmentArray(int i, noNamespace.CommitmentV3Type commitment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.CommitmentV3Type target = null;
            target = (noNamespace.CommitmentV3Type)get_store().find_element_user(COMMITMENT$10, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(commitment);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Commitment" element
     */
    public noNamespace.CommitmentV3Type insertNewCommitment(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.CommitmentV3Type target = null;
            target = (noNamespace.CommitmentV3Type)get_store().insert_element_user(COMMITMENT$10, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Commitment" element
     */
    public noNamespace.CommitmentV3Type addNewCommitment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.CommitmentV3Type target = null;
            target = (noNamespace.CommitmentV3Type)get_store().add_element_user(COMMITMENT$10);
            return target;
        }
    }
    
    /**
     * Removes the ith "Commitment" element
     */
    public void removeCommitment(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COMMITMENT$10, i);
        }
    }
    
    /**
     * Gets the "CLASSID" attribute
     */
    public int getCLASSID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CLASSID$12);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "CLASSID" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetCLASSID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(CLASSID$12);
            return target;
        }
    }
    
    /**
     * True if has "CLASSID" attribute
     */
    public boolean isSetCLASSID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(CLASSID$12) != null;
        }
    }
    
    /**
     * Sets the "CLASSID" attribute
     */
    public void setCLASSID(int classid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CLASSID$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(CLASSID$12);
            }
            target.setIntValue(classid);
        }
    }
    
    /**
     * Sets (as xml) the "CLASSID" attribute
     */
    public void xsetCLASSID(org.apache.xmlbeans.XmlInt classid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(CLASSID$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(CLASSID$12);
            }
            target.set(classid);
        }
    }
    
    /**
     * Unsets the "CLASSID" attribute
     */
    public void unsetCLASSID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(CLASSID$12);
        }
    }
}
