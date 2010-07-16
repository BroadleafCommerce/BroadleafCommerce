/*
 * XML Type:  RateV3RequestType
 * Namespace: 
 * Java type: noNamespace.RateV3RequestType
 *
 * Automatically generated - do not modify.
 */
package noNamespace;


/**
 * An XML RateV3RequestType(@).
 *
 * This is a complex type.
 */
public interface RateV3RequestType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RateV3RequestType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("ratev3requesttypeb343type");
    
    /**
     * Gets array of all "Package" elements
     */
    noNamespace.RequestPackageV3Type[] getPackageArray();
    
    /**
     * Gets ith "Package" element
     */
    noNamespace.RequestPackageV3Type getPackageArray(int i);
    
    /**
     * Returns number of "Package" element
     */
    int sizeOfPackageArray();
    
    /**
     * Sets array of all "Package" element
     */
    void setPackageArray(noNamespace.RequestPackageV3Type[] xpackageArray);
    
    /**
     * Sets ith "Package" element
     */
    void setPackageArray(int i, noNamespace.RequestPackageV3Type xpackage);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Package" element
     */
    noNamespace.RequestPackageV3Type insertNewPackage(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Package" element
     */
    noNamespace.RequestPackageV3Type addNewPackage();
    
    /**
     * Removes the ith "Package" element
     */
    void removePackage(int i);
    
    /**
     * Gets the "USERID" attribute
     */
    java.lang.String getUSERID();
    
    /**
     * Gets (as xml) the "USERID" attribute
     */
    org.apache.xmlbeans.XmlString xgetUSERID();
    
    /**
     * Sets the "USERID" attribute
     */
    void setUSERID(java.lang.String userid);
    
    /**
     * Sets (as xml) the "USERID" attribute
     */
    void xsetUSERID(org.apache.xmlbeans.XmlString userid);
    
    /**
     * Gets the "PASSWORD" attribute
     */
    java.lang.String getPASSWORD();
    
    /**
     * Gets (as xml) the "PASSWORD" attribute
     */
    org.apache.xmlbeans.XmlString xgetPASSWORD();
    
    /**
     * True if has "PASSWORD" attribute
     */
    boolean isSetPASSWORD();
    
    /**
     * Sets the "PASSWORD" attribute
     */
    void setPASSWORD(java.lang.String password);
    
    /**
     * Sets (as xml) the "PASSWORD" attribute
     */
    void xsetPASSWORD(org.apache.xmlbeans.XmlString password);
    
    /**
     * Unsets the "PASSWORD" attribute
     */
    void unsetPASSWORD();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static noNamespace.RateV3RequestType newInstance() {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static noNamespace.RateV3RequestType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static noNamespace.RateV3RequestType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static noNamespace.RateV3RequestType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static noNamespace.RateV3RequestType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static noNamespace.RateV3RequestType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static noNamespace.RateV3RequestType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static noNamespace.RateV3RequestType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static noNamespace.RateV3RequestType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static noNamespace.RateV3RequestType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.RateV3RequestType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.RateV3RequestType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.RateV3RequestType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
