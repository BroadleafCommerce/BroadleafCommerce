/*
 * XML Type:  ResponsePackageV2Type
 * Namespace: 
 * Java type: noNamespace.ResponsePackageV2Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML ResponsePackageV2Type(@).
 *
 * This is a complex type.
 */
public class ResponsePackageV2TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.ResponsePackageV2Type
{
    private static final long serialVersionUID = 1L;
    
    public ResponsePackageV2TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ZIPORIGINATION$0 = 
        new javax.xml.namespace.QName("", "ZipOrigination");
    private static final javax.xml.namespace.QName ZIPDESTINATION$2 = 
        new javax.xml.namespace.QName("", "ZipDestination");
    private static final javax.xml.namespace.QName POUNDS$4 = 
        new javax.xml.namespace.QName("", "Pounds");
    private static final javax.xml.namespace.QName OUNCES$6 = 
        new javax.xml.namespace.QName("", "Ounces");
    private static final javax.xml.namespace.QName CONTAINER$8 = 
        new javax.xml.namespace.QName("", "Container");
    private static final javax.xml.namespace.QName SIZE$10 = 
        new javax.xml.namespace.QName("", "Size");
    private static final javax.xml.namespace.QName MACHINABLE$12 = 
        new javax.xml.namespace.QName("", "Machinable");
    private static final javax.xml.namespace.QName ZONE$14 = 
        new javax.xml.namespace.QName("", "Zone");
    private static final javax.xml.namespace.QName POSTAGE$16 = 
        new javax.xml.namespace.QName("", "Postage");
    private static final javax.xml.namespace.QName RESTRICTIONS$18 = 
        new javax.xml.namespace.QName("", "Restrictions");
    private static final javax.xml.namespace.QName ERROR$20 = 
        new javax.xml.namespace.QName("", "Error");
    private static final javax.xml.namespace.QName ID$22 = 
        new javax.xml.namespace.QName("", "ID");
    
    
    /**
     * Gets the "ZipOrigination" element
     */
    public int getZipOrigination()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$0, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "ZipOrigination" element
     */
    public org.apache.xmlbeans.XmlInt xgetZipOrigination()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "ZipOrigination" element
     */
    public void setZipOrigination(int zipOrigination)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPORIGINATION$0);
            }
            target.setIntValue(zipOrigination);
        }
    }
    
    /**
     * Sets (as xml) the "ZipOrigination" element
     */
    public void xsetZipOrigination(org.apache.xmlbeans.XmlInt zipOrigination)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPORIGINATION$0);
            }
            target.set(zipOrigination);
        }
    }
    
    /**
     * Gets the "ZipDestination" element
     */
    public int getZipDestination()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$2, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "ZipDestination" element
     */
    public org.apache.xmlbeans.XmlInt xgetZipDestination()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "ZipDestination" element
     */
    public void setZipDestination(int zipDestination)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPDESTINATION$2);
            }
            target.setIntValue(zipDestination);
        }
    }
    
    /**
     * Sets (as xml) the "ZipDestination" element
     */
    public void xsetZipDestination(org.apache.xmlbeans.XmlInt zipDestination)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPDESTINATION$2);
            }
            target.set(zipDestination);
        }
    }
    
    /**
     * Gets the "Pounds" element
     */
    public int getPounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$4, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "Pounds" element
     */
    public org.apache.xmlbeans.XmlInt xgetPounds()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Pounds" element
     */
    public void setPounds(int pounds)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POUNDS$4);
            }
            target.setIntValue(pounds);
        }
    }
    
    /**
     * Sets (as xml) the "Pounds" element
     */
    public void xsetPounds(org.apache.xmlbeans.XmlInt pounds)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(POUNDS$4);
            }
            target.set(pounds);
        }
    }
    
    /**
     * Gets the "Ounces" element
     */
    public float getOunces()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$6, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Ounces" element
     */
    public org.apache.xmlbeans.XmlFloat xgetOunces()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(OUNCES$6, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Ounces" element
     */
    public void setOunces(float ounces)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OUNCES$6);
            }
            target.setFloatValue(ounces);
        }
    }
    
    /**
     * Sets (as xml) the "Ounces" element
     */
    public void xsetOunces(org.apache.xmlbeans.XmlFloat ounces)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(OUNCES$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(OUNCES$6);
            }
            target.set(ounces);
        }
    }
    
    /**
     * Gets the "Container" element
     */
    public noNamespace.ResponsePackageV2Type.Container.Enum getContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$8, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.ResponsePackageV2Type.Container.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Container" element
     */
    public noNamespace.ResponsePackageV2Type.Container xgetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV2Type.Container target = null;
            target = (noNamespace.ResponsePackageV2Type.Container)get_store().find_element_user(CONTAINER$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "Container" element
     */
    public boolean isSetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CONTAINER$8) != 0;
        }
    }
    
    /**
     * Sets the "Container" element
     */
    public void setContainer(noNamespace.ResponsePackageV2Type.Container.Enum container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTAINER$8);
            }
            target.setEnumValue(container);
        }
    }
    
    /**
     * Sets (as xml) the "Container" element
     */
    public void xsetContainer(noNamespace.ResponsePackageV2Type.Container container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV2Type.Container target = null;
            target = (noNamespace.ResponsePackageV2Type.Container)get_store().find_element_user(CONTAINER$8, 0);
            if (target == null)
            {
                target = (noNamespace.ResponsePackageV2Type.Container)get_store().add_element_user(CONTAINER$8);
            }
            target.set(container);
        }
    }
    
    /**
     * Unsets the "Container" element
     */
    public void unsetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CONTAINER$8, 0);
        }
    }
    
    /**
     * Gets the "Size" element
     */
    public noNamespace.ResponsePackageV2Type.Size.Enum getSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$10, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.ResponsePackageV2Type.Size.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Size" element
     */
    public noNamespace.ResponsePackageV2Type.Size xgetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV2Type.Size target = null;
            target = (noNamespace.ResponsePackageV2Type.Size)get_store().find_element_user(SIZE$10, 0);
            return target;
        }
    }
    
    /**
     * True if has "Size" element
     */
    public boolean isSetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SIZE$10) != 0;
        }
    }
    
    /**
     * Sets the "Size" element
     */
    public void setSize(noNamespace.ResponsePackageV2Type.Size.Enum size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIZE$10);
            }
            target.setEnumValue(size);
        }
    }
    
    /**
     * Sets (as xml) the "Size" element
     */
    public void xsetSize(noNamespace.ResponsePackageV2Type.Size size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV2Type.Size target = null;
            target = (noNamespace.ResponsePackageV2Type.Size)get_store().find_element_user(SIZE$10, 0);
            if (target == null)
            {
                target = (noNamespace.ResponsePackageV2Type.Size)get_store().add_element_user(SIZE$10);
            }
            target.set(size);
        }
    }
    
    /**
     * Unsets the "Size" element
     */
    public void unsetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SIZE$10, 0);
        }
    }
    
    /**
     * Gets the "Machinable" element
     */
    public boolean getMachinable()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$12, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Machinable" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetMachinable()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$12, 0);
            return target;
        }
    }
    
    /**
     * True if has "Machinable" element
     */
    public boolean isSetMachinable()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MACHINABLE$12) != 0;
        }
    }
    
    /**
     * Sets the "Machinable" element
     */
    public void setMachinable(boolean machinable)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MACHINABLE$12);
            }
            target.setBooleanValue(machinable);
        }
    }
    
    /**
     * Sets (as xml) the "Machinable" element
     */
    public void xsetMachinable(org.apache.xmlbeans.XmlBoolean machinable)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(MACHINABLE$12);
            }
            target.set(machinable);
        }
    }
    
    /**
     * Unsets the "Machinable" element
     */
    public void unsetMachinable()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MACHINABLE$12, 0);
        }
    }
    
    /**
     * Gets the "Zone" element
     */
    public java.lang.String getZone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZONE$14, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Zone" element
     */
    public org.apache.xmlbeans.XmlString xgetZone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZONE$14, 0);
            return target;
        }
    }
    
    /**
     * True if has "Zone" element
     */
    public boolean isSetZone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ZONE$14) != 0;
        }
    }
    
    /**
     * Sets the "Zone" element
     */
    public void setZone(java.lang.String zone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZONE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZONE$14);
            }
            target.setStringValue(zone);
        }
    }
    
    /**
     * Sets (as xml) the "Zone" element
     */
    public void xsetZone(org.apache.xmlbeans.XmlString zone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZONE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ZONE$14);
            }
            target.set(zone);
        }
    }
    
    /**
     * Unsets the "Zone" element
     */
    public void unsetZone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ZONE$14, 0);
        }
    }
    
    /**
     * Gets array of all "Postage" elements
     */
    public noNamespace.PostageV2Type[] getPostageArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(POSTAGE$16, targetList);
            noNamespace.PostageV2Type[] result = new noNamespace.PostageV2Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Postage" element
     */
    public noNamespace.PostageV2Type getPostageArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV2Type target = null;
            target = (noNamespace.PostageV2Type)get_store().find_element_user(POSTAGE$16, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "Postage" element
     */
    public int sizeOfPostageArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(POSTAGE$16);
        }
    }
    
    /**
     * Sets array of all "Postage" element
     */
    public void setPostageArray(noNamespace.PostageV2Type[] postageArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(postageArray, POSTAGE$16);
        }
    }
    
    /**
     * Sets ith "Postage" element
     */
    public void setPostageArray(int i, noNamespace.PostageV2Type postage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV2Type target = null;
            target = (noNamespace.PostageV2Type)get_store().find_element_user(POSTAGE$16, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(postage);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Postage" element
     */
    public noNamespace.PostageV2Type insertNewPostage(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV2Type target = null;
            target = (noNamespace.PostageV2Type)get_store().insert_element_user(POSTAGE$16, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Postage" element
     */
    public noNamespace.PostageV2Type addNewPostage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV2Type target = null;
            target = (noNamespace.PostageV2Type)get_store().add_element_user(POSTAGE$16);
            return target;
        }
    }
    
    /**
     * Removes the ith "Postage" element
     */
    public void removePostage(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(POSTAGE$16, i);
        }
    }
    
    /**
     * Gets the "Restrictions" element
     */
    public java.lang.String getRestrictions()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESTRICTIONS$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Restrictions" element
     */
    public org.apache.xmlbeans.XmlString xgetRestrictions()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESTRICTIONS$18, 0);
            return target;
        }
    }
    
    /**
     * True if has "Restrictions" element
     */
    public boolean isSetRestrictions()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RESTRICTIONS$18) != 0;
        }
    }
    
    /**
     * Sets the "Restrictions" element
     */
    public void setRestrictions(java.lang.String restrictions)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESTRICTIONS$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESTRICTIONS$18);
            }
            target.setStringValue(restrictions);
        }
    }
    
    /**
     * Sets (as xml) the "Restrictions" element
     */
    public void xsetRestrictions(org.apache.xmlbeans.XmlString restrictions)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESTRICTIONS$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESTRICTIONS$18);
            }
            target.set(restrictions);
        }
    }
    
    /**
     * Unsets the "Restrictions" element
     */
    public void unsetRestrictions()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RESTRICTIONS$18, 0);
        }
    }
    
    /**
     * Gets the "Error" element
     */
    public noNamespace.ErrorV2Type getError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$20, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "Error" element
     */
    public boolean isSetError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ERROR$20) != 0;
        }
    }
    
    /**
     * Sets the "Error" element
     */
    public void setError(noNamespace.ErrorV2Type error)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$20, 0);
            if (target == null)
            {
                target = (noNamespace.ErrorV2Type)get_store().add_element_user(ERROR$20);
            }
            target.set(error);
        }
    }
    
    /**
     * Appends and returns a new empty "Error" element
     */
    public noNamespace.ErrorV2Type addNewError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().add_element_user(ERROR$20);
            return target;
        }
    }
    
    /**
     * Unsets the "Error" element
     */
    public void unsetError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ERROR$20, 0);
        }
    }
    
    /**
     * Gets the "ID" attribute
     */
    public java.lang.String getID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$22);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ID" attribute
     */
    public org.apache.xmlbeans.XmlString xgetID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$22);
            return target;
        }
    }
    
    /**
     * True if has "ID" attribute
     */
    public boolean isSetID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ID$22) != null;
        }
    }
    
    /**
     * Sets the "ID" attribute
     */
    public void setID(java.lang.String id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$22);
            }
            target.setStringValue(id);
        }
    }
    
    /**
     * Sets (as xml) the "ID" attribute
     */
    public void xsetID(org.apache.xmlbeans.XmlString id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ID$22);
            }
            target.set(id);
        }
    }
    
    /**
     * Unsets the "ID" attribute
     */
    public void unsetID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ID$22);
        }
    }
    /**
     * An XML Container(@).
     *
     * This is an atomic type that is a restriction of noNamespace.ResponsePackageV2Type$Container.
     */
    public static class ContainerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.ResponsePackageV2Type.Container
    {
        private static final long serialVersionUID = 1L;
        
        public ContainerImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType, false);
        }
        
        protected ContainerImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
        {
            super(sType, b);
        }
    }
    /**
     * An XML Size(@).
     *
     * This is an atomic type that is a restriction of noNamespace.ResponsePackageV2Type$Size.
     */
    public static class SizeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.ResponsePackageV2Type.Size
    {
        private static final long serialVersionUID = 1L;
        
        public SizeImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType, false);
        }
        
        protected SizeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
        {
            super(sType, b);
        }
    }
}
