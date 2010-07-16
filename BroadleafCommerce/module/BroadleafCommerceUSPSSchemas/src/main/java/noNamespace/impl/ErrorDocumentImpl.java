/*
 * An XML document type.
 * Localname: Error
 * Namespace: 
 * Java type: noNamespace.ErrorDocument
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * A document containing one Error(@) element.
 *
 * This is a complex type.
 */
public class ErrorDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.ErrorDocument
{
    private static final long serialVersionUID = 1L;
    
    public ErrorDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ERROR$0 = 
        new javax.xml.namespace.QName("", "Error");
    
    
    /**
     * Gets the "Error" element
     */
    public noNamespace.ErrorV2Type getError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Tests for nil "Error" element
     */
    public boolean isNilError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$0, 0);
            if (target == null) return false;
            return target.isNil();
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
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$0, 0);
            if (target == null)
            {
                target = (noNamespace.ErrorV2Type)get_store().add_element_user(ERROR$0);
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
            target = (noNamespace.ErrorV2Type)get_store().add_element_user(ERROR$0);
            return target;
        }
    }
    
    /**
     * Nils the "Error" element
     */
    public void setNilError()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.ErrorV2Type target = null;
            target = (noNamespace.ErrorV2Type)get_store().find_element_user(ERROR$0, 0);
            if (target == null)
            {
                target = (noNamespace.ErrorV2Type)get_store().add_element_user(ERROR$0);
            }
            target.setNil();
        }
    }
}
