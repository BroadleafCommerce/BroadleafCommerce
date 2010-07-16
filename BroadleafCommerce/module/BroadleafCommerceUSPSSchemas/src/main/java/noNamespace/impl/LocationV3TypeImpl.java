/*
 * XML Type:  LocationV3Type
 * Namespace: 
 * Java type: noNamespace.LocationV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * An XML LocationV3Type(@).
 *
 * This is a complex type.
 */
public class LocationV3TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.LocationV3Type
{
    private static final long serialVersionUID = 1L;
    
    public LocationV3TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CUTOFF$0 = 
        new javax.xml.namespace.QName("", "CutOff");
    private static final javax.xml.namespace.QName FACILITY$2 = 
        new javax.xml.namespace.QName("", "Facility");
    private static final javax.xml.namespace.QName STREET$4 = 
        new javax.xml.namespace.QName("", "Street");
    private static final javax.xml.namespace.QName CITY$6 = 
        new javax.xml.namespace.QName("", "City");
    private static final javax.xml.namespace.QName STATE$8 = 
        new javax.xml.namespace.QName("", "State");
    private static final javax.xml.namespace.QName ZIP$10 = 
        new javax.xml.namespace.QName("", "Zip");
    
    
    /**
     * Gets the "CutOff" element
     */
    public java.lang.String getCutOff()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CUTOFF$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "CutOff" element
     */
    public org.apache.xmlbeans.XmlString xgetCutOff()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CUTOFF$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "CutOff" element
     */
    public void setCutOff(java.lang.String cutOff)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CUTOFF$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CUTOFF$0);
            }
            target.setStringValue(cutOff);
        }
    }
    
    /**
     * Sets (as xml) the "CutOff" element
     */
    public void xsetCutOff(org.apache.xmlbeans.XmlString cutOff)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CUTOFF$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CUTOFF$0);
            }
            target.set(cutOff);
        }
    }
    
    /**
     * Gets the "Facility" element
     */
    public java.lang.String getFacility()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FACILITY$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Facility" element
     */
    public org.apache.xmlbeans.XmlString xgetFacility()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FACILITY$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Facility" element
     */
    public void setFacility(java.lang.String facility)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FACILITY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FACILITY$2);
            }
            target.setStringValue(facility);
        }
    }
    
    /**
     * Sets (as xml) the "Facility" element
     */
    public void xsetFacility(org.apache.xmlbeans.XmlString facility)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FACILITY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FACILITY$2);
            }
            target.set(facility);
        }
    }
    
    /**
     * Gets the "Street" element
     */
    public java.lang.String getStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Street" element
     */
    public org.apache.xmlbeans.XmlString xgetStreet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Street" element
     */
    public void setStreet(java.lang.String street)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREET$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STREET$4);
            }
            target.setStringValue(street);
        }
    }
    
    /**
     * Sets (as xml) the "Street" element
     */
    public void xsetStreet(org.apache.xmlbeans.XmlString street)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STREET$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STREET$4);
            }
            target.set(street);
        }
    }
    
    /**
     * Gets the "City" element
     */
    public java.lang.String getCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "City" element
     */
    public org.apache.xmlbeans.XmlString xgetCity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            return target;
        }
    }
    
    /**
     * Sets the "City" element
     */
    public void setCity(java.lang.String city)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CITY$6);
            }
            target.setStringValue(city);
        }
    }
    
    /**
     * Sets (as xml) the "City" element
     */
    public void xsetCity(org.apache.xmlbeans.XmlString city)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CITY$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CITY$6);
            }
            target.set(city);
        }
    }
    
    /**
     * Gets the "State" element
     */
    public java.lang.String getState()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "State" element
     */
    public org.apache.xmlbeans.XmlString xgetState()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATE$8, 0);
            return target;
        }
    }
    
    /**
     * Sets the "State" element
     */
    public void setState(java.lang.String state)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STATE$8);
            }
            target.setStringValue(state);
        }
    }
    
    /**
     * Sets (as xml) the "State" element
     */
    public void xsetState(org.apache.xmlbeans.XmlString state)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STATE$8);
            }
            target.set(state);
        }
    }
    
    /**
     * Gets the "Zip" element
     */
    public java.lang.String getZip()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIP$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Zip" element
     */
    public org.apache.xmlbeans.XmlString xgetZip()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIP$10, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Zip" element
     */
    public void setZip(java.lang.String zip)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ZIP$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ZIP$10);
            }
            target.setStringValue(zip);
        }
    }
    
    /**
     * Sets (as xml) the "Zip" element
     */
    public void xsetZip(org.apache.xmlbeans.XmlString zip)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ZIP$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ZIP$10);
            }
            target.set(zip);
        }
    }
}
