/*
 * XML Type:  RequestPackageV2Type
 * Namespace: 
 * Java type: noNamespace.RequestPackageV2Type
 *
 * Automatically generated - do not modify.
 */
package noNamespace;


/**
 * An XML RequestPackageV2Type(@).
 *
 * This is a complex type.
 */
public interface RequestPackageV2Type extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RequestPackageV2Type.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("requestpackagev2typed576type");
    
    /**
     * Gets the "Service" element
     */
    noNamespace.RequestPackageV2Type.Service.Enum getService();
    
    /**
     * Gets (as xml) the "Service" element
     */
    noNamespace.RequestPackageV2Type.Service xgetService();
    
    /**
     * Sets the "Service" element
     */
    void setService(noNamespace.RequestPackageV2Type.Service.Enum service);
    
    /**
     * Sets (as xml) the "Service" element
     */
    void xsetService(noNamespace.RequestPackageV2Type.Service service);
    
    /**
     * Gets the "ZipOrigination" element
     */
    int getZipOrigination();
    
    /**
     * Gets (as xml) the "ZipOrigination" element
     */
    org.apache.xmlbeans.XmlInt xgetZipOrigination();
    
    /**
     * Sets the "ZipOrigination" element
     */
    void setZipOrigination(int zipOrigination);
    
    /**
     * Sets (as xml) the "ZipOrigination" element
     */
    void xsetZipOrigination(org.apache.xmlbeans.XmlInt zipOrigination);
    
    /**
     * Gets the "ZipDestination" element
     */
    int getZipDestination();
    
    /**
     * Gets (as xml) the "ZipDestination" element
     */
    org.apache.xmlbeans.XmlInt xgetZipDestination();
    
    /**
     * Sets the "ZipDestination" element
     */
    void setZipDestination(int zipDestination);
    
    /**
     * Sets (as xml) the "ZipDestination" element
     */
    void xsetZipDestination(org.apache.xmlbeans.XmlInt zipDestination);
    
    /**
     * Gets the "Pounds" element
     */
    int getPounds();
    
    /**
     * Gets (as xml) the "Pounds" element
     */
    org.apache.xmlbeans.XmlInt xgetPounds();
    
    /**
     * Sets the "Pounds" element
     */
    void setPounds(int pounds);
    
    /**
     * Sets (as xml) the "Pounds" element
     */
    void xsetPounds(org.apache.xmlbeans.XmlInt pounds);
    
    /**
     * Gets the "Ounces" element
     */
    java.lang.String getOunces();
    
    /**
     * Gets (as xml) the "Ounces" element
     */
    org.apache.xmlbeans.XmlString xgetOunces();
    
    /**
     * Sets the "Ounces" element
     */
    void setOunces(java.lang.String ounces);
    
    /**
     * Sets (as xml) the "Ounces" element
     */
    void xsetOunces(org.apache.xmlbeans.XmlString ounces);
    
    /**
     * Gets the "Container" element
     */
    noNamespace.RequestPackageV2Type.Container.Enum getContainer();
    
    /**
     * Gets (as xml) the "Container" element
     */
    noNamespace.RequestPackageV2Type.Container xgetContainer();
    
    /**
     * True if has "Container" element
     */
    boolean isSetContainer();
    
    /**
     * Sets the "Container" element
     */
    void setContainer(noNamespace.RequestPackageV2Type.Container.Enum container);
    
    /**
     * Sets (as xml) the "Container" element
     */
    void xsetContainer(noNamespace.RequestPackageV2Type.Container container);
    
    /**
     * Unsets the "Container" element
     */
    void unsetContainer();
    
    /**
     * Gets the "Size" element
     */
    noNamespace.RequestPackageV2Type.Size.Enum getSize();
    
    /**
     * Gets (as xml) the "Size" element
     */
    noNamespace.RequestPackageV2Type.Size xgetSize();
    
    /**
     * True if has "Size" element
     */
    boolean isSetSize();
    
    /**
     * Sets the "Size" element
     */
    void setSize(noNamespace.RequestPackageV2Type.Size.Enum size);
    
    /**
     * Sets (as xml) the "Size" element
     */
    void xsetSize(noNamespace.RequestPackageV2Type.Size size);
    
    /**
     * Unsets the "Size" element
     */
    void unsetSize();
    
    /**
     * Gets the "Machinable" element
     */
    boolean getMachinable();
    
    /**
     * Gets (as xml) the "Machinable" element
     */
    org.apache.xmlbeans.XmlBoolean xgetMachinable();
    
    /**
     * True if has "Machinable" element
     */
    boolean isSetMachinable();
    
    /**
     * Sets the "Machinable" element
     */
    void setMachinable(boolean machinable);
    
    /**
     * Sets (as xml) the "Machinable" element
     */
    void xsetMachinable(org.apache.xmlbeans.XmlBoolean machinable);
    
    /**
     * Unsets the "Machinable" element
     */
    void unsetMachinable();
    
    /**
     * Gets the "ID" attribute
     */
    java.lang.String getID();
    
    /**
     * Gets (as xml) the "ID" attribute
     */
    org.apache.xmlbeans.XmlString xgetID();
    
    /**
     * True if has "ID" attribute
     */
    boolean isSetID();
    
    /**
     * Sets the "ID" attribute
     */
    void setID(java.lang.String id);
    
    /**
     * Sets (as xml) the "ID" attribute
     */
    void xsetID(org.apache.xmlbeans.XmlString id);
    
    /**
     * Unsets the "ID" attribute
     */
    void unsetID();
    
    /**
     * An XML Service(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Service.
     */
    public interface Service extends org.apache.xmlbeans.XmlString
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Service.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("service0745elemtype");
        
        org.apache.xmlbeans.StringEnumAbstractBase enumValue();
        void set(org.apache.xmlbeans.StringEnumAbstractBase e);
        
        static final Enum ALL = Enum.forString("ALL");
        static final Enum FIRST_CLASS = Enum.forString("FIRST CLASS");
        static final Enum PRIORITY = Enum.forString("PRIORITY");
        static final Enum EXPRESS = Enum.forString("EXPRESS");
        static final Enum BPM = Enum.forString("BPM");
        static final Enum PARCEL = Enum.forString("PARCEL");
        static final Enum MEDIA = Enum.forString("MEDIA");
        static final Enum LIBRARY = Enum.forString("LIBRARY");
        
        static final int INT_ALL = Enum.INT_ALL;
        static final int INT_FIRST_CLASS = Enum.INT_FIRST_CLASS;
        static final int INT_PRIORITY = Enum.INT_PRIORITY;
        static final int INT_EXPRESS = Enum.INT_EXPRESS;
        static final int INT_BPM = Enum.INT_BPM;
        static final int INT_PARCEL = Enum.INT_PARCEL;
        static final int INT_MEDIA = Enum.INT_MEDIA;
        static final int INT_LIBRARY = Enum.INT_LIBRARY;
        
        /**
         * Enumeration value class for noNamespace.RequestPackageV2Type$Service.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_ALL
         * Enum.forString(s); // returns the enum value for a string
         * Enum.forInt(i); // returns the enum value for an int
         * </pre>
         * Enumeration objects are immutable singleton objects that
         * can be compared using == object equality. They have no
         * public constructor. See the constants defined within this
         * class for all the valid values.
         */
        static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
        {
            /**
             * Returns the enum value for a string, or null if none.
             */
            public static Enum forString(java.lang.String s)
                { return (Enum)table.forString(s); }
            /**
             * Returns the enum value corresponding to an int, or null if none.
             */
            public static Enum forInt(int i)
                { return (Enum)table.forInt(i); }
            
            private Enum(java.lang.String s, int i)
                { super(s, i); }
            
            static final int INT_ALL = 1;
            static final int INT_FIRST_CLASS = 2;
            static final int INT_PRIORITY = 3;
            static final int INT_EXPRESS = 4;
            static final int INT_BPM = 5;
            static final int INT_PARCEL = 6;
            static final int INT_MEDIA = 7;
            static final int INT_LIBRARY = 8;
            
            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table
            (
                new Enum[]
                {
                    new Enum("ALL", INT_ALL),
                    new Enum("FIRST CLASS", INT_FIRST_CLASS),
                    new Enum("PRIORITY", INT_PRIORITY),
                    new Enum("EXPRESS", INT_EXPRESS),
                    new Enum("BPM", INT_BPM),
                    new Enum("PARCEL", INT_PARCEL),
                    new Enum("MEDIA", INT_MEDIA),
                    new Enum("LIBRARY", INT_LIBRARY),
                }
            );
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() { return forInt(intValue()); } 
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static noNamespace.RequestPackageV2Type.Service newValue(java.lang.Object obj) {
              return (noNamespace.RequestPackageV2Type.Service) type.newValue( obj ); }
            
            public static noNamespace.RequestPackageV2Type.Service newInstance() {
              return (noNamespace.RequestPackageV2Type.Service) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static noNamespace.RequestPackageV2Type.Service newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (noNamespace.RequestPackageV2Type.Service) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * An XML Container(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Container.
     */
    public interface Container extends org.apache.xmlbeans.XmlString
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Container.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("container31f9elemtype");
        
        org.apache.xmlbeans.StringEnumAbstractBase enumValue();
        void set(org.apache.xmlbeans.StringEnumAbstractBase e);
        
        static final Enum FLAT_RATE_BOX = Enum.forString("Flat Rate Box");
        static final Enum FLAT_RATE_ENVELOPE = Enum.forString("Flat Rate Envelope");
        
        static final int INT_FLAT_RATE_BOX = Enum.INT_FLAT_RATE_BOX;
        static final int INT_FLAT_RATE_ENVELOPE = Enum.INT_FLAT_RATE_ENVELOPE;
        
        /**
         * Enumeration value class for noNamespace.RequestPackageV2Type$Container.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_FLAT_RATE_BOX
         * Enum.forString(s); // returns the enum value for a string
         * Enum.forInt(i); // returns the enum value for an int
         * </pre>
         * Enumeration objects are immutable singleton objects that
         * can be compared using == object equality. They have no
         * public constructor. See the constants defined within this
         * class for all the valid values.
         */
        static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
        {
            /**
             * Returns the enum value for a string, or null if none.
             */
            public static Enum forString(java.lang.String s)
                { return (Enum)table.forString(s); }
            /**
             * Returns the enum value corresponding to an int, or null if none.
             */
            public static Enum forInt(int i)
                { return (Enum)table.forInt(i); }
            
            private Enum(java.lang.String s, int i)
                { super(s, i); }
            
            static final int INT_FLAT_RATE_BOX = 1;
            static final int INT_FLAT_RATE_ENVELOPE = 2;
            
            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table
            (
                new Enum[]
                {
                    new Enum("Flat Rate Box", INT_FLAT_RATE_BOX),
                    new Enum("Flat Rate Envelope", INT_FLAT_RATE_ENVELOPE),
                }
            );
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() { return forInt(intValue()); } 
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static noNamespace.RequestPackageV2Type.Container newValue(java.lang.Object obj) {
              return (noNamespace.RequestPackageV2Type.Container) type.newValue( obj ); }
            
            public static noNamespace.RequestPackageV2Type.Container newInstance() {
              return (noNamespace.RequestPackageV2Type.Container) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static noNamespace.RequestPackageV2Type.Container newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (noNamespace.RequestPackageV2Type.Container) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * An XML Size(@).
     *
     * This is an atomic type that is a restriction of noNamespace.RequestPackageV2Type$Size.
     */
    public interface Size extends org.apache.xmlbeans.XmlString
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Size.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s3503F9A07BB1731BF5BC1281AADEB8D0").resolveHandle("size4829elemtype");
        
        org.apache.xmlbeans.StringEnumAbstractBase enumValue();
        void set(org.apache.xmlbeans.StringEnumAbstractBase e);
        
        static final Enum REGULAR = Enum.forString("REGULAR");
        static final Enum LARGE = Enum.forString("LARGE");
        static final Enum OVERSIZE = Enum.forString("OVERSIZE");
        
        static final int INT_REGULAR = Enum.INT_REGULAR;
        static final int INT_LARGE = Enum.INT_LARGE;
        static final int INT_OVERSIZE = Enum.INT_OVERSIZE;
        
        /**
         * Enumeration value class for noNamespace.RequestPackageV2Type$Size.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_REGULAR
         * Enum.forString(s); // returns the enum value for a string
         * Enum.forInt(i); // returns the enum value for an int
         * </pre>
         * Enumeration objects are immutable singleton objects that
         * can be compared using == object equality. They have no
         * public constructor. See the constants defined within this
         * class for all the valid values.
         */
        static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
        {
            /**
             * Returns the enum value for a string, or null if none.
             */
            public static Enum forString(java.lang.String s)
                { return (Enum)table.forString(s); }
            /**
             * Returns the enum value corresponding to an int, or null if none.
             */
            public static Enum forInt(int i)
                { return (Enum)table.forInt(i); }
            
            private Enum(java.lang.String s, int i)
                { super(s, i); }
            
            static final int INT_REGULAR = 1;
            static final int INT_LARGE = 2;
            static final int INT_OVERSIZE = 3;
            
            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table
            (
                new Enum[]
                {
                    new Enum("REGULAR", INT_REGULAR),
                    new Enum("LARGE", INT_LARGE),
                    new Enum("OVERSIZE", INT_OVERSIZE),
                }
            );
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() { return forInt(intValue()); } 
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static noNamespace.RequestPackageV2Type.Size newValue(java.lang.Object obj) {
              return (noNamespace.RequestPackageV2Type.Size) type.newValue( obj ); }
            
            public static noNamespace.RequestPackageV2Type.Size newInstance() {
              return (noNamespace.RequestPackageV2Type.Size) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static noNamespace.RequestPackageV2Type.Size newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (noNamespace.RequestPackageV2Type.Size) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static noNamespace.RequestPackageV2Type newInstance() {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static noNamespace.RequestPackageV2Type newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static noNamespace.RequestPackageV2Type parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static noNamespace.RequestPackageV2Type parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static noNamespace.RequestPackageV2Type parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static noNamespace.RequestPackageV2Type parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static noNamespace.RequestPackageV2Type parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.RequestPackageV2Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static noNamespace.RequestPackageV2Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (noNamespace.RequestPackageV2Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
