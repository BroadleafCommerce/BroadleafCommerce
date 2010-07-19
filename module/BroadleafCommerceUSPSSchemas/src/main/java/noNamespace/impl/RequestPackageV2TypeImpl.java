/*
 * XML Type:  RequestPackageV2Type
 * Namespace: 
 * Java type: noNamespace.RequestPackageV2Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML RequestPackageV2Type(@).
 *
 * This is a complex type.
 */
public class RequestPackageV2TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RequestPackageV2Type
{
    private static final long serialVersionUID = 1L;
    
    public RequestPackageV2TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SERVICE$0 = 
        new javax.xml.namespace.QName("", "Service");
    private static final javax.xml.namespace.QName ZIPORIGINATION$2 = 
        new javax.xml.namespace.QName("", "ZipOrigination");
    private static final javax.xml.namespace.QName ZIPDESTINATION$4 = 
        new javax.xml.namespace.QName("", "ZipDestination");
    private static final javax.xml.namespace.QName POUNDS$6 = 
        new javax.xml.namespace.QName("", "Pounds");
    private static final javax.xml.namespace.QName OUNCES$8 = 
        new javax.xml.namespace.QName("", "Ounces");
    private static final javax.xml.namespace.QName CONTAINER$10 = 
        new javax.xml.namespace.QName("", "Container");
    private static final javax.xml.namespace.QName SIZE$12 = 
        new javax.xml.namespace.QName("", "Size");
    private static final javax.xml.namespace.QName MACHINABLE$14 = 
        new javax.xml.namespace.QName("", "Machinable");
    private static final javax.xml.namespace.QName ID$16 = 
        new javax.xml.namespace.QName("", "ID");
    
    
    /**
     * Gets the "Service" element
     */
    public noNamespace.RequestPackageV2Type.Service.Enum getService()
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
            return (noNamespace.RequestPackageV2Type.Service.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Service" element
     */
    public noNamespace.RequestPackageV2Type.Service xgetService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Service target = null;
            target = (noNamespace.RequestPackageV2Type.Service)get_store().find_element_user(SERVICE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Service" element
     */
    public void setService(noNamespace.RequestPackageV2Type.Service.Enum service)
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
    public void xsetService(noNamespace.RequestPackageV2Type.Service service)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Service target = null;
            target = (noNamespace.RequestPackageV2Type.Service)get_store().find_element_user(SERVICE$0, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV2Type.Service)get_store().add_element_user(SERVICE$0);
            }
            target.set(service);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$2, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$2, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPORIGINATION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPORIGINATION$2);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPORIGINATION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPORIGINATION$2);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$4, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$4, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIPDESTINATION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIPDESTINATION$4);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(ZIPDESTINATION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(ZIPDESTINATION$4);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$6, 0);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$6, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POUNDS$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POUNDS$6);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(POUNDS$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(POUNDS$6);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$8, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OUNCES$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OUNCES$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OUNCES$8);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OUNCES$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OUNCES$8);
            }
            target.set(ounces);
        }
    }
    
    /**
     * Gets the "Container" element
     */
    public noNamespace.RequestPackageV2Type.Container.Enum getContainer()
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
            return (noNamespace.RequestPackageV2Type.Container.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Container" element
     */
    public noNamespace.RequestPackageV2Type.Container xgetContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Container target = null;
            target = (noNamespace.RequestPackageV2Type.Container)get_store().find_element_user(CONTAINER$10, 0);
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
    public void setContainer(noNamespace.RequestPackageV2Type.Container.Enum container)
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
    public void xsetContainer(noNamespace.RequestPackageV2Type.Container container)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Container target = null;
            target = (noNamespace.RequestPackageV2Type.Container)get_store().find_element_user(CONTAINER$10, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV2Type.Container)get_store().add_element_user(CONTAINER$10);
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
    public noNamespace.RequestPackageV2Type.Size.Enum getSize()
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
            return (noNamespace.RequestPackageV2Type.Size.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Size" element
     */
    public noNamespace.RequestPackageV2Type.Size xgetSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Size target = null;
            target = (noNamespace.RequestPackageV2Type.Size)get_store().find_element_user(SIZE$12, 0);
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
    public void setSize(noNamespace.RequestPackageV2Type.Size.Enum size)
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
    public void xsetSize(noNamespace.RequestPackageV2Type.Size size)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RequestPackageV2Type.Size target = null;
            target = (noNamespace.RequestPackageV2Type.Size)get_store().find_element_user(SIZE$12, 0);
            if (target == null)
            {
                target = (noNamespace.RequestPackageV2Type.Size)get_store().add_element_user(SIZE$12);
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
     * Gets the "ID" attribute
     */
    public java.lang.String getID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$16);
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
            return get_store().find_attribute_user(ID$16) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$16);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ID$16);
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
            get_store().remove_attribute(ID$16);
        }
    }
    /**
     * An XML Service(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Service.
     */
    public static class ServiceImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV2Type.Service
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
     * An XML Container(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Container.
     */
    public static class ContainerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV2Type.Container
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
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Size.
     */
    public static class SizeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements noNamespace.RequestPackageV2Type.Size
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
