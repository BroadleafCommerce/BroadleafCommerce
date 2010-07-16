/*
 * XML Type:  PostageV3Type
 * Namespace: 
 * Java type: noNamespace.PostageV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace;


/**
 * An XML PostageV3Type(@).
 *
 * This is a complex type.
 */
public interface PostageV3Type extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PostageV3Type.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("postagev3type28adtype");
    
    /**
     * Gets the "MailService" element
     */
    java.lang.String getMailService();
    
    /**
     * Gets (as xml) the "MailService" element
     */
    org.apache.xmlbeans.XmlString xgetMailService();
    
    /**
     * Sets the "MailService" element
     */
    void setMailService(java.lang.String mailService);
    
    /**
     * Sets (as xml) the "MailService" element
     */
    void xsetMailService(org.apache.xmlbeans.XmlString mailService);
    
    /**
     * Gets the "Rate" element
     */
    float getRate();
    
    /**
     * Gets (as xml) the "Rate" element
     */
    org.apache.xmlbeans.XmlFloat xgetRate();
    
    /**
     * Sets the "Rate" element
     */
    void setRate(float rate);
    
    /**
     * Sets (as xml) the "Rate" element
     */
    void xsetRate(org.apache.xmlbeans.XmlFloat rate);
    
    /**
     * Gets the "CommercialRate" element
     */
    float getCommercialRate();
    
    /**
     * Gets (as xml) the "CommercialRate" element
     */
    org.apache.xmlbeans.XmlFloat xgetCommercialRate();
    
    /**
     * True if has "CommercialRate" element
     */
    boolean isSetCommercialRate();
    
    /**
     * Sets the "CommercialRate" element
     */
    void setCommercialRate(float commercialRate);
    
    /**
     * Sets (as xml) the "CommercialRate" element
     */
    void xsetCommercialRate(org.apache.xmlbeans.XmlFloat commercialRate);
    
    /**
     * Unsets the "CommercialRate" element
     */
    void unsetCommercialRate();
    
    /**
     * Gets the "CommitmentDate" element
     */
    java.lang.String getCommitmentDate();
    
    /**
     * Gets (as xml) the "CommitmentDate" element
     */
    org.apache.xmlbeans.XmlString xgetCommitmentDate();
    
    /**
     * Sets the "CommitmentDate" element
     */
    void setCommitmentDate(java.lang.String commitmentDate);
    
    /**
     * Sets (as xml) the "CommitmentDate" element
     */
    void xsetCommitmentDate(org.apache.xmlbeans.XmlString commitmentDate);
    
    /**
     * Gets array of all "Location" elements
     */
    noNamespace.LocationV3Type[] getLocationArray();
    
    /**
     * Gets ith "Location" element
     */
    noNamespace.LocationV3Type getLocationArray(int i);
    
    /**
     * Returns number of "Location" element
     */
    int sizeOfLocationArray();
    
    /**
     * Sets array of all "Location" element
     */
    void setLocationArray(noNamespace.LocationV3Type[] locationArray);
    
    /**
     * Sets ith "Location" element
     */
    void setLocationArray(int i, noNamespace.LocationV3Type location);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Location" element
     */
    noNamespace.LocationV3Type insertNewLocation(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Location" element
     */
    noNamespace.LocationV3Type addNewLocation();
    
    /**
     * Removes the ith "Location" element
     */
    void removeLocation(int i);
    
    /**
     * Gets array of all "Commitment" elements
     */
    noNamespace.CommitmentV3Type[] getCommitmentArray();
    
    /**
     * Gets ith "Commitment" element
     */
    noNamespace.CommitmentV3Type getCommitmentArray(int i);
    
    /**
     * Returns number of "Commitment" element
     */
    int sizeOfCommitmentArray();
    
    /**
     * Sets array of all "Commitment" element
     */
    void setCommitmentArray(noNamespace.CommitmentV3Type[] commitmentArray);
    
    /**
     * Sets ith "Commitment" element
     */
    void setCommitmentArray(int i, noNamespace.CommitmentV3Type commitment);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Commitment" element
     */
    noNamespace.CommitmentV3Type insertNewCommitment(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Commitment" element
     */
    noNamespace.CommitmentV3Type addNewCommitment();
    
    /**
     * Removes the ith "Commitment" element
     */
    void removeCommitment(int i);
    
    /**
     * Gets the "CLASSID" attribute
     */
    int getCLASSID();
    
    /**
     * Gets (as xml) the "CLASSID" attribute
     */
    org.apache.xmlbeans.XmlInt xgetCLASSID();
    
    /**
     * True if has "CLASSID" attribute
     */
    boolean isSetCLASSID();
    
    /**
     * Sets the "CLASSID" attribute
     */
    void setCLASSID(int classid);
    
    /**
     * Sets (as xml) the "CLASSID" attribute
     */
    void xsetCLASSID(org.apache.xmlbeans.XmlInt classid);
    
    /**
     * Unsets the "CLASSID" attribute
     */
    void unsetCLASSID();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static noNamespace.PostageV3Type newInstance() {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static noNamespace.PostageV3Type newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static noNamespace.PostageV3Type parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static noNamespace.PostageV3Type parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static noNamespace.PostageV3Type parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static noNamespace.PostageV3Type parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static noNamespace.PostageV3Type parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static noNamespace.PostageV3Type parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static noNamespace.PostageV3Type parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static noNamespace.PostageV3Type parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static noNamespace.PostageV3Type parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static noNamespace.PostageV3Type parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static noNamespace.PostageV3Type parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static noNamespace.PostageV3Type parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static noNamespace.PostageV3Type parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static noNamespace.PostageV3Type parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.PostageV3Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.PostageV3Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.PostageV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
