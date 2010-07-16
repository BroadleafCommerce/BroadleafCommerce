/*
 * An XML document type.
 * Localname: RateV3Request
 * Namespace: 
 * Java type: noNamespace.RateV3RequestDocument
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * A document containing one RateV3Request(@) element.
 *
 * This is a complex type.
 */
public class RateV3RequestDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RateV3RequestDocument
{
    private static final long serialVersionUID = 1L;
    
    public RateV3RequestDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName RATEV3REQUEST$0 = 
        new javax.xml.namespace.QName("", "RateV3Request");
    
    
    /**
     * Gets the "RateV3Request" element
     */
    public noNamespace.RateV3RequestType getRateV3Request()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3RequestType target = null;
            target = (noNamespace.RateV3RequestType)get_store().find_element_user(RATEV3REQUEST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "RateV3Request" element
     */
    public void setRateV3Request(noNamespace.RateV3RequestType rateV3Request)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3RequestType target = null;
            target = (noNamespace.RateV3RequestType)get_store().find_element_user(RATEV3REQUEST$0, 0);
            if (target == null)
            {
                target = (noNamespace.RateV3RequestType)get_store().add_element_user(RATEV3REQUEST$0);
            }
            target.set(rateV3Request);
        }
    }
    
    /**
     * Appends and returns a new empty "RateV3Request" element
     */
    public noNamespace.RateV3RequestType addNewRateV3Request()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV3RequestType target = null;
            target = (noNamespace.RateV3RequestType)get_store().add_element_user(RATEV3REQUEST$0);
            return target;
        }
    }
}
