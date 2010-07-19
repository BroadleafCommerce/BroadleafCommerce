/*
 * XML Type:  RequestPackageV3Type
 * Namespace: 
 * Java type: noNamespace.RequestPackageV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML RequestPackageV3Type(@).
 *
 * This is a complex type.
 */
public class RequestPackageV3TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RequestPackageV3Type
{
    private static final long serialVersionUID = 1L;
    
    public RequestPackageV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SERVICE$0 = 
        new javax.xml.namespace.QName("", "Service");
    private static final javax.xml.namespace.QName FIRSTCLASSMAILTYPE$2 = 
        new javax.xml.namespace.QName("", "FirstClassMailType");
    private static final javax.xml.namespace.QName ZIPORIGINATION$4 = 
        new javax.xml.namespace.QName("", "ZipOrigination");
    private static final javax.xml.namespace.QName ZIPDESTINATION$6 = 
        new javax.xml.namespace.QName("", "ZipDestination");
    private static final javax.xml.namespace.QName POUNDS$8 = 
        new javax.xml.namespace.QName("", "Pounds");
    private static final javax.xml.namespace.QName OUNCES$10 = 
        new javax.xml.namespace.QName("", "Ounces");
    private static final javax.xml.namespace.QName CONTAINER$12 = 
        new javax.xml.namespace.QName("", "Container");
    private static final javax.xml.namespace.QName SIZE$14 = 
        new javax.xml.namespace.QName("", "Size");
    private static final javax.xml.namespace.QName MACHINABLE$16 = 
        new javax.xml.namespace.QName("", "Machinable");
    private static final javax.xml.namespace.QName WIDTH$18 = 
        new javax.xml.namespace.QName("", "Width");
    private static final javax.xml.namespace.QName LENGTH$20 = 
        new javax.xml.namespace.QName("", "Length");
    private static final javax.xml.namespace.QName HEIGHT$22 = 
        new javax.xml.namespace.QName("", "Height");
    private static final javax.xml.namespace.QName GIRTH$24 = 
        new javax.xml.namespace.QName("", "Girth");
    private static final javax.xml.namespace.QName RETURNLOCATIONS$26 = 
        new javax.xml.namespace.QName("", "ReturnLocations");
    private static final javax.xml.namespace.QName SHIPDATE$28 = 
        new javax.xml.namespace.QName("", "ShipDate");
    private static final javax.xml.namespace.QName ID$30 = 
        new javax.xml.namespace.QName("", "ID");
    
    
    /**
     * Gets the "Service" element
     */
    public noNamespace.RequestPackageV3Type.Service.Enum getService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SERVICE$0, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.RequestPackageV3Type.Service.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Service" element
     */
    public noNamespace.RequestPackageV3Type.Service xgetService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Service target = null;
            target = (noNamespace.RequestPackageV3Type.Service)get_store().find_element_user(SERVICE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Service" element
     */
    public void setService(noNamespace.RequestPackageV3Type.Service.Enum service)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SERVICE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SERVICE$0);
            }
            target.setEnumValue(service);
        }
    }
    
    /**
     * Sets (as xml) the "Service" element
     */
    public void xsetService(noNamespace.RequestPackageV3Type.Service service)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Service target = null;
            target = (noNamespace.RequestPackageV3Type.Service)get_store().find_element_user(SERVICE$0, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV3Type.Service)get_store().add_element_user(SERVICE$0);
            }
            target.set(service);
        }
    }
    
    /**
     * Gets the "FirstClassMailType" element
     */
    public noNamespace.RequestPackageV3Type.FirstClassMailType.Enum getFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTCLASSMAILTYPE$2, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.RequestPackageV3Type.FirstClassMailType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "FirstClassMailType" element
     */
    public noNamespace.RequestPackageV3Type.FirstClassMailType xgetFirstClassMailType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.FirstClassMailType target = null;
            target = (noNamespace.RequestPackageV3Type.FirstClassMailType)get_store().find_element_user(FIRSTCLASSMAILTYPE$2, 0);
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
            return get_store().count_elements(FIRSTCLASSMAILTYPE$2) != 0;
        }
    }
    
    /**
     * Sets the "FirstClassMailType" element
     */
    public void setFirstClassMailType(noNamespace.RequestPackageV3Type.FirstClassMailType.Enum firstClassMailType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTCLASSMAILTYPE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FIRSTCLASSMAILTYPE$2);
            }
            target.setEnumValue(firstClassMailType);
        }
    }
    
    /**
     * Sets (as xml) the "FirstClassMailType" element
     */
    public void xsetFirstClassMailType(noNamespace.RequestPackageV3Type.FirstClassMailType firstClassMailType)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.FirstClassMailType target = null;
            target = (noNamespace.RequestPackageV3Type.FirstClassMailType)get_store().find_element_user(FIRSTCLASSMAILTYPE$2, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV3Type.FirstClassMailType)get_store().add_element_user(FIRSTCLASSMAILTYPE$2);
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
            get_store().remove_element(FIRSTCLASSMAILTYPE$2, 0);
        }
    }
    
    /**
     * Gets the "ZipOrigination" element
     */
    public int getZipOrigination()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$4, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPORIGINATION$4);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPORIGINATION$4);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$6, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPDESTINATION$6);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPDESTINATION$6);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$8, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POUNDS$8);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(POUNDS$8);
            }
            target.set(pounds);
        }
    }
    
    /**
     * Gets the "Ounces" element
     */
    public java.lang.String getOunces()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Ounces" element
     */
    public org.apache.xmlbeans.XmlString xgetOunces()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OUNCES$10, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Ounces" element
     */
    public void setOunces(java.lang.String ounces)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OUNCES$10);
            }
            target.setStringValue(ounces);
        }
    }
    
    /**
     * Sets (as xml) the "Ounces" element
     */
    public void xsetOunces(org.apache.xmlbeans.XmlString ounces)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OUNCES$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OUNCES$10);
            }
            target.set(ounces);
        }
    }
    
    /**
     * Gets the "Container" element
     */
    public noNamespace.RequestPackageV3Type.Container.Enum getContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$12, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.RequestPackageV3Type.Container.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Container" element
     */
    public noNamespace.RequestPackageV3Type.Container xgetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Container target = null;
            target = (noNamespace.RequestPackageV3Type.Container)get_store().find_element_user(CONTAINER$12, 0);
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
            return get_store().count_elements(CONTAINER$12) != 0;
        }
    }
    
    /**
     * Sets the "Container" element
     */
    public void setContainer(noNamespace.RequestPackageV3Type.Container.Enum container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONTAINER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONTAINER$12);
            }
            target.setEnumValue(container);
        }
    }
    
    /**
     * Sets (as xml) the "Container" element
     */
    public void xsetContainer(noNamespace.RequestPackageV3Type.Container container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Container target = null;
            target = (noNamespace.RequestPackageV3Type.Container)get_store().find_element_user(CONTAINER$12, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV3Type.Container)get_store().add_element_user(CONTAINER$12);
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
            get_store().remove_element(CONTAINER$12, 0);
        }
    }
    
    /**
     * Gets the "Size" element
     */
    public noNamespace.RequestPackageV3Type.Size.Enum getSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$14, 0);
            if (target == null)
            {
                return null;
            }
            return (noNamespace.RequestPackageV3Type.Size.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Size" element
     */
    public noNamespace.RequestPackageV3Type.Size xgetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Size target = null;
            target = (noNamespace.RequestPackageV3Type.Size)get_store().find_element_user(SIZE$14, 0);
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
            return get_store().count_elements(SIZE$14) != 0;
        }
    }
    
    /**
     * Sets the "Size" element
     */
    public void setSize(noNamespace.RequestPackageV3Type.Size.Enum size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIZE$14);
            }
            target.setEnumValue(size);
        }
    }
    
    /**
     * Sets (as xml) the "Size" element
     */
    public void xsetSize(noNamespace.RequestPackageV3Type.Size size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV3Type.Size target = null;
            target = (noNamespace.RequestPackageV3Type.Size)get_store().find_element_user(SIZE$14, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV3Type.Size)get_store().add_element_user(SIZE$14);
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
            get_store().remove_element(SIZE$14, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$16, 0);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$16, 0);
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
            return get_store().count_elements(MACHINABLE$16) != 0;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MACHINABLE$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MACHINABLE$16);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(MACHINABLE$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(MACHINABLE$16);
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
            get_store().remove_element(MACHINABLE$16, 0);
        }
    }
    
    /**
     * Gets the "Width" element
     */
    public java.lang.String getWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WIDTH$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Width" element
     */
    public org.apache.xmlbeans.XmlString xgetWidth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WIDTH$18, 0);
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
            return get_store().count_elements(WIDTH$18) != 0;
        }
    }
    
    /**
     * Sets the "Width" element
     */
    public void setWidth(java.lang.String width)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WIDTH$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WIDTH$18);
            }
            target.setStringValue(width);
        }
    }
    
    /**
     * Sets (as xml) the "Width" element
     */
    public void xsetWidth(org.apache.xmlbeans.XmlString width)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WIDTH$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WIDTH$18);
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
            get_store().remove_element(WIDTH$18, 0);
        }
    }
    
    /**
     * Gets the "Length" element
     */
    public java.lang.String getLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LENGTH$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Length" element
     */
    public org.apache.xmlbeans.XmlString xgetLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LENGTH$20, 0);
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
            return get_store().count_elements(LENGTH$20) != 0;
        }
    }
    
    /**
     * Sets the "Length" element
     */
    public void setLength(java.lang.String length)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LENGTH$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LENGTH$20);
            }
            target.setStringValue(length);
        }
    }
    
    /**
     * Sets (as xml) the "Length" element
     */
    public void xsetLength(org.apache.xmlbeans.XmlString length)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LENGTH$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LENGTH$20);
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
            get_store().remove_element(LENGTH$20, 0);
        }
    }
    
    /**
     * Gets the "Height" element
     */
    public java.lang.String getHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HEIGHT$22, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Height" element
     */
    public org.apache.xmlbeans.XmlString xgetHeight()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HEIGHT$22, 0);
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
            return get_store().count_elements(HEIGHT$22) != 0;
        }
    }
    
    /**
     * Sets the "Height" element
     */
    public void setHeight(java.lang.String height)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(HEIGHT$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(HEIGHT$22);
            }
            target.setStringValue(height);
        }
    }
    
    /**
     * Sets (as xml) the "Height" element
     */
    public void xsetHeight(org.apache.xmlbeans.XmlString height)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(HEIGHT$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(HEIGHT$22);
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
            get_store().remove_element(HEIGHT$22, 0);
        }
    }
    
    /**
     * Gets the "Girth" element
     */
    public java.lang.String getGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GIRTH$24, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Girth" element
     */
    public org.apache.xmlbeans.XmlString xgetGirth()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GIRTH$24, 0);
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
            return get_store().count_elements(GIRTH$24) != 0;
        }
    }
    
    /**
     * Sets the "Girth" element
     */
    public void setGirth(java.lang.String girth)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GIRTH$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GIRTH$24);
            }
            target.setStringValue(girth);
        }
    }
    
    /**
     * Sets (as xml) the "Girth" element
     */
    public void xsetGirth(org.apache.xmlbeans.XmlString girth)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(GIRTH$24, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(GIRTH$24);
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
            get_store().remove_element(GIRTH$24, 0);
        }
    }
    
    /**
     * Gets the "ReturnLocations" element
     */
    public boolean getReturnLocations()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RETURNLOCATIONS$26, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "ReturnLocations" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetReturnLocations()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(RETURNLOCATIONS$26, 0);
            return target;
        }
    }
    
    /**
     * True if has "ReturnLocations" element
     */
    public boolean isSetReturnLocations()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RETURNLOCATIONS$26) != 0;
        }
    }
    
    /**
     * Sets the "ReturnLocations" element
     */
    public void setReturnLocations(boolean returnLocations)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RETURNLOCATIONS$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RETURNLOCATIONS$26);
            }
            target.setBooleanValue(returnLocations);
        }
    }
    
    /**
     * Sets (as xml) the "ReturnLocations" element
     */
    public void xsetReturnLocations(org.apache.xmlbeans.XmlBoolean returnLocations)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(RETURNLOCATIONS$26, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(RETURNLOCATIONS$26);
            }
            target.set(returnLocations);
        }
    }
    
    /**
     * Unsets the "ReturnLocations" element
     */
    public void unsetReturnLocations()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RETURNLOCATIONS$26, 0);
        }
    }
    
    /**
     * Gets the "ShipDate" element
     */
    public noNamespace.ShipDateV3Type getShipDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ShipDateV3Type target = null;
            target = (noNamespace.ShipDateV3Type)get_store().find_element_user(SHIPDATE$28, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "ShipDate" element
     */
    public boolean isSetShipDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SHIPDATE$28) != 0;
        }
    }
    
    /**
     * Sets the "ShipDate" element
     */
    public void setShipDate(noNamespace.ShipDateV3Type shipDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ShipDateV3Type target = null;
            target = (noNamespace.ShipDateV3Type)get_store().find_element_user(SHIPDATE$28, 0);
            if (target == null)
            {
                target = (noNamespace.ShipDateV3Type)get_store().add_element_user(SHIPDATE$28);
            }
            target.set(shipDate);
        }
    }
    
    /**
     * Appends and returns a new empty "ShipDate" element
     */
    public noNamespace.ShipDateV3Type addNewShipDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ShipDateV3Type target = null;
            target = (noNamespace.ShipDateV3Type)get_store().add_element_user(SHIPDATE$28);
            return target;
        }
    }
    
    /**
     * Unsets the "ShipDate" element
     */
    public void unsetShipDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SHIPDATE$28, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$30);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$30);
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
            return get_store().find_attribute_user(ID$30) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$30);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ID$30);
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
            get_store().remove_attribute(ID$30);
        }
    }
    /**
     * An XML Service(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV3Type$Service.
     */
    public static class ServiceImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV3Type.Service
    {
        private static final long serialVersionUID = 1L;
        
        public ServiceImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType, false);
        }
        
        protected ServiceImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
        {
            super(sType, b);
        }
    }
    /**
     * An XML FirstClassMailType(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV3Type$FirstClassMailType.
     */
    public static class FirstClassMailTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV3Type.FirstClassMailType
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
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV3Type$Container.
     */
    public static class ContainerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV3Type.Container
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
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV3Type$Size.
     */
    public static class SizeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV3Type.Size
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
