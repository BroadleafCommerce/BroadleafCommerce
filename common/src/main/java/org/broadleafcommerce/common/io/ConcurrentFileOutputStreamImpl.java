/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.common.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static java.nio.file.StandardCopyOption.*;

@Component("blConcurrentFileOutputStream")
public class ConcurrentFileOutputStreamImpl implements ConcurrentFileOutputStream {
    private static final Map<String, Object> FILE_MOVE_LOCKS = new WeakHashMap<>();

    @Value("${asset.server.file.buffer.size:8192}")
    protected int defaultFileBufferSize;

    @Override
    public int write(InputStream src, File dest) throws IOException {
        return write(src, dest, defaultFileBufferSize);
    }

    public int write(InputStream src, File dest, int bufferSize) throws IOException {
        File tempFile = createTempFile(dest);
        int totalWrote = writeToTempFile(src, tempFile, bufferSize);
        replaceExisting(tempFile, dest);
        return totalWrote;
    }

    protected File createTempFile(File dest) throws IOException {
        File tempFile = new File(dest.getAbsolutePath() + getTempFileSuffix());
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        return tempFile;
    }

    protected String getTempFileSuffix() {
        return ".temp-" + UUID.randomUUID();
    }

    protected int writeToTempFile(InputStream src, File tempFile, int bufferSize) throws IOException {
        FileOutputStream fos = new FileOutputStream(tempFile);

        int totalWrote = 0;

        int readBytes;
        byte[] bytes = new byte[bufferSize];
        while ((readBytes = src.read(bytes)) != -1) {
            fos.write(bytes, 0, readBytes);
            totalWrote += readBytes;
        }

        fos.close();

        return totalWrote;
    }

    protected void replaceExisting(File src, File dest) throws IOException {
        synchronized (getFileMoveLock(dest)) {
            if (src.exists()) {
                try {
                    Files.move(src.toPath(), dest.toPath(), ATOMIC_MOVE, REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(src.toPath(), dest.toPath(), REPLACE_EXISTING);
                }
            }
        }
    }

    protected Object getFileMoveLock(File file) {
        String filePath = file.getAbsolutePath();
        synchronized (FILE_MOVE_LOCKS) {
            if (!FILE_MOVE_LOCKS.containsKey(filePath)) {
                FILE_MOVE_LOCKS.put(filePath, new Object());
            }
            return FILE_MOVE_LOCKS.get(filePath);
        }
    }
}
