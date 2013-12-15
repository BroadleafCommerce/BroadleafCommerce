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
package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.file.domain.FileWorkArea;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class BroadleafFileServiceImplTest extends TestCase {

    private BroadleafFileServiceImpl bfs = new BroadleafFileServiceImpl();
    private FileSystemFileServiceProvider fsp = new FileSystemFileServiceProvider();
    private FileWorkArea baseSystemDirectory;

    public void setUp() throws Exception {
        fsp.maxGeneratedDirectoryDepth = 2;
        bfs.defaultFileServiceProvider = fsp;
        bfs.maxGeneratedDirectoryDepth = 2;
        baseSystemDirectory = bfs.initializeWorkArea();

        // Use the FileServiceProvider to create a temporary directory and use it as the 
        // location to store files.   
        fsp.fileSystemBaseDirectory = baseSystemDirectory.filePathLocation;
    }

    public void tearDown() throws Exception {
        // Close the work area used as the main directory for files. 
        bfs.closeWorkArea(baseSystemDirectory);
    }

    public void testCreateWorkArea() throws Exception {
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation());

        // The service should return a directory that is ready write to.
        assertTrue(f1.exists());

        // The service should return a unique work area.
        FileWorkArea workArea2 = bfs.initializeWorkArea();
        assertFalse(workArea2.getFilePathLocation().equals(workArea1.getFilePathLocation()));

        // Remove the work areas
        bfs.closeWorkArea(workArea1);
        assertFalse(f1.exists());

        bfs.closeWorkArea(workArea2);

    }

    public void testCreateAddFile() throws Exception {        
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation() + "test.txt");
        FileWriter fw = new FileWriter(f1);
        fw.append("Test File");
        fw.close();
        
        bfs.addOrUpdateResource(workArea1, f1, false);
        
        bfs.closeWorkArea(workArea1);

        File resource = bfs.getResource("test.txt");

        assertTrue(resource.exists());

        bfs.removeResource("test.txt");

        resource = bfs.getResource("test.txt");
        assertFalse(resource.exists());
    }

    public void testCreateAddFiles() throws Exception {
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation() + "test2.txt");
        FileWriter fw = new FileWriter(f1);
        fw.append("Test File 2");
        fw.close();

        File f2 = new File(workArea1.getFilePathLocation() + "test3.txt");
        FileWriter fw2 = new FileWriter(f2);
        fw2.append("Test File 3");
        fw2.close();

        List<File> files = new ArrayList<File>();
        files.add(f1);
        files.add(f2);

        bfs.addOrUpdateResources(workArea1, files, false);
        bfs.closeWorkArea(workArea1);

        File resource = bfs.getResource("test2.txt");
        assertTrue(resource.exists());

        resource = bfs.getResource("test3.txt");
        assertTrue(resource.exists());

        bfs.removeResource("test2.txt");
        bfs.removeResource("test3.txt");

        resource = bfs.getResource("test3.txt");
        assertFalse(resource.exists());
    }

    public void testCreateFilesCopyWorkarea() throws Exception {
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation() + "test4.txt");
        FileWriter fw = new FileWriter(f1);
        fw.append("Test File 4");
        fw.close();

        File f2 = new File(workArea1.getFilePathLocation() + "test5.txt");
        FileWriter fw2 = new FileWriter(f2);
        fw2.append("Test File 5");
        fw2.close();

        bfs.addOrUpdateResources(workArea1, false);
        bfs.closeWorkArea(workArea1);

        File resource = bfs.getResource("test4.txt");
        assertTrue(resource.exists());

        resource = bfs.getResource("test5.txt");
        assertTrue(resource.exists());

        bfs.removeResource("test4.txt");
        bfs.removeResource("test5.txt");

        resource = bfs.getResource("test5.txt");
        assertFalse(resource.exists());
    }
}