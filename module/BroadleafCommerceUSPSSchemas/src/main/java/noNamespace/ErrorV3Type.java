/*
 * XML Type:  ErrorV3Type
 * Namespace: 
 * Java type: noNamespace.ErrorV3Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace;


/**
 * An XML ErrorV3Type(@).
 *
 * This is a complex type.
 */
public interface ErrorV3Type extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ErrorV3Type.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("errorv3typed556type");
    
    /**
     * Gets the "Number" element
     */
    int getNumber();
    
    /**
     * Gets (as xml) the "Number" element
     */
    org.apache.xmlbeans.XmlInt xgetNumber();
    
    /**
     * Sets the "Number" element
     */
    void setNumber(int number);
    
    /**
     * Sets (as xml) the "Number" element
     */
    void xsetNumber(org.apache.xmlbeans.XmlInt number);
    
    /**
     * Gets the "Source" element
     */
    java.lang.String getSource();
    
    /**
     * Gets (as xml) the "Source" element
     */
    org.apache.xmlbeans.XmlString xgetSource();
    
    /**
     * Sets the "Source" element
     */
    void setSource(java.lang.String source);
    
    /**
     * Sets (as xml) the "Source" element
     */
    void xsetSource(org.apache.xmlbeans.XmlString source);
    
    /**
     * Gets the "Description" element
     */
    java.lang.String getDescription();
    
    /**
     * Gets (as xml) the "Description" element
     */
    org.apache.xmlbeans.XmlString xgetDescription();
    
    /**
     * Sets the "Description" element
     */
    void setDescription(java.lang.String description);
    
    /**
     * Sets (as xml) the "Description" element
     */
    void xsetDescription(org.apache.xmlbeans.XmlString description);
    
    /**
     * Gets the "HelpFile" element
     */
    java.lang.String getHelpFile();
    
    /**
     * Gets (as xml) the "HelpFile" element
     */
    org.apache.xmlbeans.XmlString xgetHelpFile();
    
    /**
     * True if has "HelpFile" element
     */
    boolean isSetHelpFile();
    
    /**
     * Sets the "HelpFile" element
     */
    void setHelpFile(java.lang.String helpFile);
    
    /**
     * Sets (as xml) the "HelpFile" element
     */
    void xsetHelpFile(org.apache.xmlbeans.XmlString helpFile);
    
    /**
     * Unsets the "HelpFile" element
     */
    void unsetHelpFile();
    
    /**
     * Gets the "HelpContext" element
     */
    java.lang.String getHelpContext();
    
    /**
     * Gets (as xml) the "HelpContext" element
     */
    org.apache.xmlbeans.XmlString xgetHelpContext();
    
    /**
     * True if has "HelpContext" element
     */
    boolean isSetHelpContext();
    
    /**
     * Sets the "HelpContext" element
     */
    void setHelpContext(java.lang.String helpContext);
    
    /**
     * Sets (as xml) the "HelpContext" element
     */
    void xsetHelpContext(org.apache.xmlbeans.XmlString helpContext);
    
    /**
     * Unsets the "HelpContext" element
     */
    void unsetHelpContext();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static noNamespace.ErrorV3Type newInstance() {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static noNamespace.ErrorV3Type newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static noNamespace.ErrorV3Type parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static noNamespace.ErrorV3Type parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static noNamespace.ErrorV3Type parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static noNamespace.ErrorV3Type parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static noNamespace.ErrorV3Type parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static noNamespace.ErrorV3Type parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static noNamespace.ErrorV3Type parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static noNamespace.ErrorV3Type parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.ErrorV3Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.ErrorV3Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.ErrorV3Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
