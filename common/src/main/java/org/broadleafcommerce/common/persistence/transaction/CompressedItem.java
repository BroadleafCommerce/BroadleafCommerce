/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.springframework.security.crypto.codec.Base64;

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
    protected boolean decompressInToString = true;

    public CompressedItem(String start, boolean decompressInToString) throws IOException {
        this.decompressInToString = decompressInToString;
        this.compressed = compress(start);
    }

    public CompressedItem(byte[] compressed, boolean decompressInToString) {
        this.decompressInToString = decompressInToString;
        this.compressed = compressed;
    }

    public String decompress() throws IOException {
        return decompress(compressed);
    }

    public boolean isDecompressInToString() {
        return decompressInToString;
    }

    public void setDecompressInToString(boolean decompressInToString) {
        this.decompressInToString = decompressInToString;
    }

    public byte[] getCompressed() {
        return compressed;
    }

    @Override
    public String toString() {
        String response = null;
        if (decompressInToString) {
            try {
                response = decompress(compressed);
            } catch (IOException e) {
                //do nothing
            }
        } else {
            response = compressed!=null?new String(Base64.encode(compressed)):"" + "\n";
        }
        return response;
    }
}
