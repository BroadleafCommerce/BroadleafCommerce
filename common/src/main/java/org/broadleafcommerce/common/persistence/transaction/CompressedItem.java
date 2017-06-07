package org.broadleafcommerce.common.persistence.transaction;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple POJO for storing a String value as a compressed byte array.
 *
 * @author Jeff Fischer
 */
public class CompressedItem {

    public static byte[] compress(final String str) throws IOException {
        if ((str == null) || (str.length() == 0)) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return obj.toByteArray();
    }

    public static String decompress(final byte[] compressed) throws IOException {
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    protected byte[] compressed;

    public CompressedItem(String start) throws IOException {
        this.compressed = compress(start);
    }

    public String decompress() throws IOException {
        return decompress(compressed);
    }

    @Override
    public String toString() {
        String response = null;
        try {
            response = decompress(compressed);
        } catch (IOException e) {
            //do nothing
        }
        return response;
    }
}
