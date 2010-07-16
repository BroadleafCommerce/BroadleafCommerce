/*
 * An XML document type.
 * Localname: RateV2Request
 * Namespace: 
 * Java type: noNamespace.RateV2RequestDocument
 *
 * Automatically generated - do not modify.
 */
package noNamespace.impl;
/**
 * A document containing one RateV2Request(@) element.
 *
 * This is a complex type.
 */
public class RateV2RequestDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements noNamespace.RateV2RequestDocument
{
    private static final long serialVersionUID = 1L;
    
    public RateV2RequestDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName RATEV2REQUEST$0 = 
        new javax.xml.namespace.QName("", "RateV2Request");
    
    
    /**
     * Gets the "RateV2Request" element
     */
    public noNamespace.RateV2RequestType getRateV2Request()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2RequestType target = null;
            target = (noNamespace.RateV2RequestType)get_store().find_element_user(RATEV2REQUEST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "RateV2Request" element
     */
    public void setRateV2Request(noNamespace.RateV2RequestType rateV2Request)
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2RequestType target = null;
            target = (noNamespace.RateV2RequestType)get_store().find_element_user(RATEV2REQUEST$0, 0);
            if (target == null)
            {
                target = (noNamespace.RateV2RequestType)get_store().add_element_user(RATEV2REQUEST$0);
            }
            target.set(rateV2Request);
        }
    }
    
    /**
     * Appends and returns a new empty "RateV2Request" element
     */
    public noNamespace.RateV2RequestType addNewRateV2Request()
    {
        synchronized (monitor())
        {
            check_orphaned();
            noNamespace.RateV2RequestType target = null;
            target = (noNamespace.RateV2RequestType)get_store().add_element_user(RATEV2REQUEST$0);
            return target;
        }
    }
}
