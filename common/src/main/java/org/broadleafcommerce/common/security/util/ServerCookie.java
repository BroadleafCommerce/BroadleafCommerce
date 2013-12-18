/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.security.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This is a modified version of the ServerCookie implementation taken from
 * the Apache Tomcat source. This class allows Broadleaf to properly construct
 * cookies for different browsers and include the httpOnly protection as well.
 * 
 * @author jfischer
 *
 */
public class ServerCookie {

    private static final String tspecials = ",; ";
    private static final String tspecials2 = "()<>@,;:\\\"/[]?={} \t";
    private static final String tspecials2NoSlash = "()<>@,;:\\\"[]?={} \t";

    // Other fields
    private static final String OLD_COOKIE_PATTERN =
        "EEE, dd-MMM-yyyy HH:mm:ss z";
    private static final ThreadLocal<DateFormat> OLD_COOKIE_FORMAT =
        new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat df =
                new SimpleDateFormat(OLD_COOKIE_PATTERN, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };
    private static final String ancientDate;
    static {
        ancientDate = OLD_COOKIE_FORMAT.get().format(new Date(10000));
    }

    /**
     * If set to true, we parse cookies according to the servlet spec,
     */
    public static final boolean STRICT_SERVLET_COMPLIANCE = false;

    /**
     * If set to false, we don't use the IE6/7 Max-Age/Expires work around
     */
    public static final boolean ALWAYS_ADD_EXPIRES = true;

    // TODO RFC2965 fields also need to be passed
    public static void appendCookieValue( StringBuffer headerBuf,
            int version,
            String name,
            String value,
            String path,
            String domain,
            String comment,
            int maxAge,
            boolean isSecure,
            boolean isHttpOnly)
    {
        StringBuffer buf = new StringBuffer();
        // Servlet implementation checks name
        buf.append( name );
        buf.append("=");
        // Servlet implementation does not check anything else

        version = maybeQuote2(version, buf, value,true);

        // Add version 1 specific information
        if (version == 1) {
            // Version=1 ... required
            buf.append ("; Version=1");

            // Comment=comment
            if ( comment!=null ) {
                buf.append ("; Comment=");
                maybeQuote2(version, buf, comment);
            }
        }

        // Add domain information, if present
        if (domain!=null) {
            buf.append("; Domain=");
            maybeQuote2(version, buf, domain);
        }

        // Max-Age=secs ... or use old "Expires" format
        // TODO RFC2965 Discard
        if (maxAge >= 0) {
            if (version > 0) {
                buf.append ("; Max-Age=");
                buf.append (maxAge);
            }
            // IE6, IE7 and possibly other browsers don't understand Max-Age.
            // They do understand Expires, even with V1 cookies!
            if (version == 0 || ALWAYS_ADD_EXPIRES) {
                // Wdy, DD-Mon-YY HH:MM:SS GMT ( Expires Netscape format )
                buf.append ("; Expires=");
                // To expire immediately we need to set the time in past
                if (maxAge == 0)
                    buf.append( ancientDate );
                else
                    OLD_COOKIE_FORMAT.get().format(
                            new Date(System.currentTimeMillis() +
                                    maxAge*1000L),
                                    buf, new FieldPosition(0));
            }
        }

        // Path=path
        if (path!=null) {
            buf.append ("; Path=");
            if (version==0) {
                maybeQuote2(version, buf, path);
            } else {
                maybeQuote2(version, buf, path, ServerCookie.tspecials2NoSlash, false);
            }
        }

        // Secure
        if (isSecure) {
            buf.append ("; Secure");
        }

        // HttpOnly
        if (isHttpOnly) {
            buf.append("; HttpOnly");
        }
        headerBuf.append(buf);
    }

    public static int maybeQuote2 (int version, StringBuffer buf, String value) {
        return maybeQuote2(version,buf,value,false);
    }

    public static int maybeQuote2 (int version, StringBuffer buf, String value, boolean allowVersionSwitch) {
        return maybeQuote2(version,buf,value,null,allowVersionSwitch);
    }

    public static int maybeQuote2 (int version, StringBuffer buf, String value, String literals, boolean allowVersionSwitch) {
        if (value==null || value.length()==0) {
            buf.append("\"\"");
        }else if (containsCTL(value,version))
            throw new IllegalArgumentException("Control character in cookie value, consider BASE64 encoding your value");
        else if (alreadyQuoted(value)) {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value,1,value.length()-1));
            buf.append('"');
        } else if (allowVersionSwitch && (!STRICT_SERVLET_COMPLIANCE) && version==0 && !isToken2(value, literals)) {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value,0,value.length()));
            buf.append('"');
            version = 1;
        } else if (version==0 && !isToken(value,literals)) {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value,0,value.length()));
            buf.append('"');
        } else if (version==1 && !isToken2(value,literals)) {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value,0,value.length()));
            buf.append('"');
        }else {
            buf.append(value);
        }
        return version;
    }

    public static boolean containsCTL(String value, int version) {
        if( value==null) return false;
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f) {
                if (c == 0x09)
                    continue; //allow horizontal tabs
                return true;
            }
        }
        return false;
    }

    public static boolean alreadyQuoted (String value) {
        if (value==null || value.length()==0) return false;
        return (value.charAt(0)=='\"' && value.charAt(value.length()-1)=='\"');
    }

    /**
     * Escapes any double quotes in the given string.
     *
     * @param s the input string
     * @param beginIndex start index inclusive
     * @param endIndex exclusive
     * @return The (possibly) escaped string
     */
    private static String escapeDoubleQuotes(String s, int beginIndex, int endIndex) {

        if (s == null || s.length() == 0 || s.indexOf('"') == -1) {
            return s;
        }

        StringBuffer b = new StringBuffer();
        for (int i = beginIndex; i < endIndex; i++) {
            char c = s.charAt(i);
            if (c == '\\' ) {
                b.append(c);
                //ignore the character after an escape, just append it
                if (++i>=endIndex) throw new IllegalArgumentException("Invalid escape character in cookie value.");
                b.append(s.charAt(i));
            } else if (c == '"')
                b.append('\\').append('"');
            else
                b.append(c);
        }

        return b.toString();
    }

    /*
     * Tests a string and returns true if the string counts as a
     * reserved token in the Java language.
     *
     * @param value the <code>String</code> to be tested
     *
     * @return      <code>true</code> if the <code>String</code> is a reserved
     *              token; <code>false</code> if it is not
     */
    public static boolean isToken(String value) {
        return isToken(value,null);
    }

    public static boolean isToken(String value, String literals) {
        String tspecials = (literals==null?ServerCookie.tspecials:literals);
        if( value==null) return true;
        int len = value.length();

        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);

            if (tspecials.indexOf(c) != -1)
                return false;
        }
        return true;
    }

    public static boolean isToken2(String value) {
        return isToken2(value,null);
    }

    public static boolean isToken2(String value, String literals) {
        String tspecials2 = (literals==null?ServerCookie.tspecials2:literals);
        if( value==null) return true;
        int len = value.length();

        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (tspecials2.indexOf(c) != -1)
                return false;
        }
        return true;
    }
}
