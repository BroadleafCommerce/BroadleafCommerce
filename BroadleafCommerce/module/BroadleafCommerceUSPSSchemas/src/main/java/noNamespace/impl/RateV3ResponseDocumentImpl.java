/*
 * An XML document type.
 * Localname: RateV3Response
 * Namespace: 
 * Java type: noNamespace.RateV3ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * A document containing one RateV3Response(@) element.
 *
 * This is a complex type.
 */
public class RateV3ResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RateV3ResponseDocument
{
    private static final long serialVersionUID = 1L;
    
    public RateV3ResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName RATEV3RESPONSE$0 = 
        new javax.xml.namespace.QName("", "RateV3Response");
    
    
    /**
     * Gets the "RateV3Response" element
     */
    public noNamespace.RateV3ResponseType getRateV3Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3ResponseType target = null;
            target = (noNamespace.RateV3ResponseType)get_store().find_element_user(RATEV3RESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "RateV3Response" element
     */
    public void setRateV3Response(noNamespace.RateV3ResponseType rateV3Response)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3ResponseType target = null;
            target = (noNamespace.RateV3ResponseType)get_store().find_element_user(RATEV3RESPONSE$0, 0);
            if (target == null)
            {
                target = (noNamespace.RateV3ResponseType)get_store().add_element_user(RATEV3RESPONSE$0);
            }
            target.set(rateV3Response);
        }
    }
    
    /**
     * Appends and returns a new empty "RateV3Response" element
     */
    public noNamespace.RateV3ResponseType addNewRateV3Response()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3ResponseType target = null;
            target = (noNamespace.RateV3ResponseType)get_store().add_element_user(RATEV3RESPONSE$0);
            return target;
        }
    }
}
