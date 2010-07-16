/*
 * XML Type:  ResponsePackageV3Type
 * Namespace: 
 * Java type: noNamespace.ResponsePackageV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML ResponsePackageV3Type(@).
 *
 * This is a complex type.
 */
public class ResponsePackageV3TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.ResponsePackageV3Type
{
    private static final long serialVersionUID = 1L;
    
    public ResponsePackageV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
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
    private static final javax.xml.namespace.QName FIRSTCLASSMAILTYPE$8 = 
        new javax.xml.namespace.QName("", "FirstClassMailType");
    private static final javax.xml.namespace.QName CONTAINER$10 = 
        new javax.xml.namespace.QName("", "Container");
    private static final javax.xml.namespace.QName SIZE$12 = 
        new javax.xml.namespace.QName("", "Size");
    private static final javax.xml.namespace.QName MACHINABLE$14 = 
        new javax.xml.namespace.QName("", "Machinable");
    private static final javax.xml.namespace.QName WIDTH$16 = 
        new javax.xml.namespace.QName("", "Width");
    private static final javax.xml.namespace.QName LENGTH$18 = 
        new javax.xml.namespace.QName("", "Length");
    private static final javax.xml.namespace.QName HEIGHT$20 = 
        new javax.xml.namespace.QName("", "Height");
    private static final javax.xml.namespace.QName GIRTH$22 = 
        new javax.xml.namespace.QName("", "Girth");
    private static final javax.xml.namespace.QName ZONE$24 = 
        new javax.xml.namespace.QName("", "Zone");
    private static final javax.xml.namespace.QName POSTAGE$26 = 
        new javax.xml.namespace.QName("", "Postage");
    private static final javax.xml.namespace.QName RESTRICTIONS$28 = 
        new javax.xml.namespace.QName("", "Restrictions");
    private static final javax.xml.namespace.QName ERROR$30 = 
        new javax.xml.namespace.QName("", "Error");
    private static final javax.xml.namespace.QName ID$32 = 
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
     * Gets the "FirstClassMailType" element
     */
    public noNamespace.ResponsePackageV3Type.FirstClassMailType.Enum getFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTCLASSMAILTYPE$8, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.ResponsePackageV3Type.FirstClassMailType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "FirstClassMailType" element
     */
    public noNamespace.ResponsePackageV3Type.FirstClassMailType xgetFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.FirstClassMailType target = null;
            target = (noNamespace.ResponsePackageV3Type.FirstClassMailType)get_store().find_element_user(FIRSTCLASSMAILTYPE$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "FirstClassMailType" element
     */
    public boolean isSetFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FIRSTCLASSMAILTYPE$8) != 0;
        }
    }
    
    /**
     * Sets the "FirstClassMailType" element
     */
    public void setFirstClassMailType(noNamespace.ResponsePackageV3Type.FirstClassMailType.Enum firstClassMailType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTCLASSMAILTYPE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FIRSTCLASSMAILTYPE$8);
            }
            target.setEnumValue(firstClassMailType);
        }
    }
    
    /**
     * Sets (as xml) the "FirstClassMailType" element
     */
    public void xsetFirstClassMailType(noNamespace.ResponsePackageV3Type.FirstClassMailType firstClassMailType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.FirstClassMailType target = null;
            target = (noNamespace.ResponsePackageV3Type.FirstClassMailType)get_store().find_element_user(FIRSTCLASSMAILTYPE$8, 0);
            if (target == null)
            {
                target = (noNamespace.ResponsePackageV3Type.FirstClassMailType)get_store().add_element_user(FIRSTCLASSMAILTYPE$8);
            }
            target.set(firstClassMailType);
        }
    }
    
    /**
     * Unsets the "FirstClassMailType" element
     */
    public void unsetFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FIRSTCLASSMAILTYPE$8, 0);
        }
    }
    
    /**
     * Gets the "Container" element
     */
    public noNamespace.ResponsePackageV3Type.Container.Enum getContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$10, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.ResponsePackageV3Type.Container.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Container" element
     */
    public noNamespace.ResponsePackageV3Type.Container xgetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.Container target = null;
            target = (noNamespace.ResponsePackageV3Type.Container)get_store().find_element_user(CONTAINER$10, 0);
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
            return get_store().count_elements(CONTAINER$10) != 0;
        }
    }
    
    /**
     * Sets the "Container" element
     */
    public void setContainer(noNamespace.ResponsePackageV3Type.Container.Enum container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTAINER$10);
            }
            target.setEnumValue(container);
        }
    }
    
    /**
     * Sets (as xml) the "Container" element
     */
    public void xsetContainer(noNamespace.ResponsePackageV3Type.Container container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.Container target = null;
            target = (noNamespace.ResponsePackageV3Type.Container)get_store().find_element_user(CONTAINER$10, 0);
            if (target == null)
            {
                target = (noNamespace.ResponsePackageV3Type.Container)get_store().add_element_user(CONTAINER$10);
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
            get_store().remove_element(CONTAINER$10, 0);
        }
    }
    
    /**
     * Gets the "Size" element
     */
    public noNamespace.ResponsePackageV3Type.Size.Enum getSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$12, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.ResponsePackageV3Type.Size.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Size" element
     */
    public noNamespace.ResponsePackageV3Type.Size xgetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.Size target = null;
            target = (noNamespace.ResponsePackageV3Type.Size)get_store().find_element_user(SIZE$12, 0);
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
            return get_store().count_elements(SIZE$12) != 0;
        }
    }
    
    /**
     * Sets the "Size" element
     */
    public void setSize(noNamespace.ResponsePackageV3Type.Size.Enum size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIZE$12);
            }
            target.setEnumValue(size);
        }
    }
    
    /**
     * Sets (as xml) the "Size" element
     */
    public void xsetSize(noNamespace.ResponsePackageV3Type.Size size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ResponsePackageV3Type.Size target = null;
            target = (noNamespace.ResponsePackageV3Type.Size)get_store().find_element_user(SIZE$12, 0);
            if (target == null)
            {
                target = (noNamespace.ResponsePackageV3Type.Size)get_store().add_element_user(SIZE$12);
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
            get_store().remove_element(SIZE$12, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$14, 0);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$14, 0);
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
            return get_store().count_elements(MACHINABLE$14) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MACHINABLE$14);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(MACHINABLE$14);
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
            get_store().remove_element(MACHINABLE$14, 0);
        }
    }
    
    /**
     * Gets the "Width" element
     */
    public float getWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WIDTH$16, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Width" element
     */
    public org.apache.xmlbeans.XmlFloat xgetWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(WIDTH$16, 0);
            return target;
        }
    }
    
    /**
     * True if has "Width" element
     */
    public boolean isSetWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(WIDTH$16) != 0;
        }
    }
    
    /**
     * Sets the "Width" element
     */
    public void setWidth(float width)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WIDTH$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WIDTH$16);
            }
            target.setFloatValue(width);
        }
    }
    
    /**
     * Sets (as xml) the "Width" element
     */
    public void xsetWidth(org.apache.xmlbeans.XmlFloat width)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(WIDTH$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(WIDTH$16);
            }
            target.set(width);
        }
    }
    
    /**
     * Unsets the "Width" element
     */
    public void unsetWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(WIDTH$16, 0);
        }
    }
    
    /**
     * Gets the "Length" element
     */
    public float getLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LENGTH$18, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Length" element
     */
    public org.apache.xmlbeans.XmlFloat xgetLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(LENGTH$18, 0);
            return target;
        }
    }
    
    /**
     * True if has "Length" element
     */
    public boolean isSetLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LENGTH$18) != 0;
        }
    }
    
    /**
     * Sets the "Length" element
     */
    public void setLength(float length)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LENGTH$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LENGTH$18);
            }
            target.setFloatValue(length);
        }
    }
    
    /**
     * Sets (as xml) the "Length" element
     */
    public void xsetLength(org.apache.xmlbeans.XmlFloat length)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(LENGTH$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(LENGTH$18);
            }
            target.set(length);
        }
    }
    
    /**
     * Unsets the "Length" element
     */
    public void unsetLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LENGTH$18, 0);
        }
    }
    
    /**
     * Gets the "Height" element
     */
    public float getHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HEIGHT$20, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Height" element
     */
    public org.apache.xmlbeans.XmlFloat xgetHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(HEIGHT$20, 0);
            return target;
        }
    }
    
    /**
     * True if has "Height" element
     */
    public boolean isSetHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(HEIGHT$20) != 0;
        }
    }
    
    /**
     * Sets the "Height" element
     */
    public void setHeight(float height)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HEIGHT$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(HEIGHT$20);
            }
            target.setFloatValue(height);
        }
    }
    
    /**
     * Sets (as xml) the "Height" element
     */
    public void xsetHeight(org.apache.xmlbeans.XmlFloat height)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(HEIGHT$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(HEIGHT$20);
            }
            target.set(height);
        }
    }
    
    /**
     * Unsets the "Height" element
     */
    public void unsetHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(HEIGHT$20, 0);
        }
    }
    
    /**
     * Gets the "Girth" element
     */
    public float getGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GIRTH$22, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "Girth" element
     */
    public org.apache.xmlbeans.XmlFloat xgetGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(GIRTH$22, 0);
            return target;
        }
    }
    
    /**
     * True if has "Girth" element
     */
    public boolean isSetGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GIRTH$22) != 0;
        }
    }
    
    /**
     * Sets the "Girth" element
     */
    public void setGirth(float girth)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GIRTH$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GIRTH$22);
            }
            target.setFloatValue(girth);
        }
    }
    
    /**
     * Sets (as xml) the "Girth" element
     */
    public void xsetGirth(org.apache.xmlbeans.XmlFloat girth)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(GIRTH$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(GIRTH$22);
            }
            target.set(girth);
        }
    }
    
    /**
     * Unsets the "Girth" element
     */
    public void unsetGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GIRTH$22, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZONE$24, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZONE$24, 0);
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
            return get_store().count_elements(ZONE$24) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZONE$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZONE$24);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZONE$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ZONE$24);
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
            get_store().remove_element(ZONE$24, 0);
        }
    }
    
    /**
     * Gets array of all "Postage" elements
     */
    public noNamespace.PostageV3Type[] getPostageArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(POSTAGE$26, targetList);
            noNamespace.PostageV3Type[] result = new noNamespace.PostageV3Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "Postage" element
     */
    public noNamespace.PostageV3Type getPostageArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV3Type target = null;
            target = (noNamespace.PostageV3Type)get_store().find_element_user(POSTAGE$26, i);
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
            return get_store().count_elements(POSTAGE$26);
        }
    }
    
    /**
     * Sets array of all "Postage" element
     */
    public void setPostageArray(noNamespace.PostageV3Type[] postageArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(postageArray, POSTAGE$26);
        }
    }
    
    /**
     * Sets ith "Postage" element
     */
    public void setPostageArray(int i, noNamespace.PostageV3Type postage)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV3Type target = null;
            target = (noNamespace.PostageV3Type)get_store().find_element_user(POSTAGE$26, i);
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
    public noNamespace.PostageV3Type insertNewPostage(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV3Type target = null;
            target = (noNamespace.PostageV3Type)get_store().insert_element_user(POSTAGE$26, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Postage" element
     */
    public noNamespace.PostageV3Type addNewPostage()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.PostageV3Type target = null;
            target = (noNamespace.PostageV3Type)get_store().add_element_user(POSTAGE$26);
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
            get_store().remove_element(POSTAGE$26, i);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESTRICTIONS$28, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESTRICTIONS$28, 0);
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
            return get_store().count_elements(RESTRICTIONS$28) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RESTRICTIONS$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RESTRICTIONS$28);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(RESTRICTIONS$28, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(RESTRICTIONS$28);
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
            get_store().remove_element(RESTRICTIONS$28, 0);
        }
    }
    
    /**
     * Gets the "Error" element
     */
    public noNamespace.ErrorV3Type getError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV3Type target = null;
            target = (noNamespace.ErrorV3Type)get_store().find_element_user(ERROR$30, 0);
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
            return get_store().count_elements(ERROR$30) != 0;
        }
    }
    
    /**
     * Sets the "Error" element
     */
    public void setError(noNamespace.ErrorV3Type error)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV3Type target = null;
            target = (noNamespace.ErrorV3Type)get_store().find_element_user(ERROR$30, 0);
            if (target == null)
            {
                target = (noNamespace.ErrorV3Type)get_store().add_element_user(ERROR$30);
            }
            target.set(error);
        }
    }
    
    /**
     * Appends and returns a new empty "Error" element
     */
    public noNamespace.ErrorV3Type addNewError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV3Type target = null;
            target = (noNamespace.ErrorV3Type)get_store().add_element_user(ERROR$30);
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
            get_store().remove_element(ERROR$30, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$32);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$32);
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
            return get_store().find_attribute_user(ID$32) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$32);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ID$32);
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
            get_store().remove_attribute(ID$32);
        }
    }
    /**
     * An XML FirstClassMailType(@).
     *
     * This is an atomic type that is a restriction of noNamespace.ResponsePackageV3Type$FirstClassMailType.
     */
    public static class FirstClassMailTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.ResponsePackageV3Type.FirstClassMailType
    {
        private static final long serialVersionUID = 1L;
        
        public FirstClassMailTypeImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType, false);
        }
        
        protected FirstClassMailTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
        {
            super(sType, b);
        }
    }
    /**
     * An XML Container(@).
     *
     * This is an atomic type that is a restriction of noNamespace.ResponsePackageV3Type$Container.
     */
    public static class ContainerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.ResponsePackageV3Type.Container
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
     * This is an atomic type that is a restriction of noNamespace.ResponsePackageV3Type$Size.
     */
    public static class SizeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.ResponsePackageV3Type.Size
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
