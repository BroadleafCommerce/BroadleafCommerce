/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class BroadleafFileServiceImplTest {

    private final BroadleafFileServiceImpl bfs = new BroadleafFileServiceImpl();
    private final FileSystemFileServiceProvider fsp = new FileSystemFileServiceProvider();
    private FileWorkArea baseSystemDirectory;

    @BeforeEach
    public void setUp() throws Exception {
        fsp.maxGeneratedDirectoryDepth = 2;
        bfs.defaultFileServiceProvider = fsp;
        bfs.maxGeneratedDirectoryDepth = 2;
        baseSystemDirectory = bfs.initializeWorkArea();

        // Use the FileServiceProvider to create a temporary directory and use it as the
        // location to store files.
        fsp.fileSystemBaseDirectory = baseSystemDirectory.getFilePathLocation();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Close the work area used as the main directory for files.
        bfs.closeWorkArea(baseSystemDirectory);
    }

    @Test
    public void testCreateWorkArea() throws Exception {
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation());

        // The service should return a directory that is ready write to.
        Assertions.assertTrue(f1.exists());

        // The service should return a unique work area.
        FileWorkArea workArea2 = bfs.initializeWorkArea();
        Assertions.assertNotEquals(workArea2.getFilePathLocation(), workArea1.getFilePathLocation());

        // Remove the work areas
        bfs.closeWorkArea(workArea1);
        Assertions.assertFalse(f1.exists());

        bfs.closeWorkArea(workArea2);
    }

    @Test
    public void testCreateAddFile() throws Exception {
        FileWorkArea workArea1 = bfs.initializeWorkArea();
        File f1 = new File(workArea1.getFilePathLocation() + "test.txt");
        FileWriter fw = new FileWriter(f1);
        fw.append("Test File");
        fw.close();

        bfs.addOrUpdateResource(workArea1, f1, false);

        bfs.closeWorkArea(workArea1);

        File resource = bfs.getResource("test.txt");

        Assertions.assertTrue(resource.exists());

        bfs.removeResource("test.txt");

        resource = bfs.getResource("test.txt");
        Assertions.assertFalse(resource.exists());
    }

    @Test
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

        List<File> files = new ArrayList<>();
        files.add(f1);
        files.add(f2);

        bfs.addOrUpdateResources(workArea1, files, false);
        bfs.closeWorkArea(workArea1);

        File resource = bfs.getResource("test2.txt");
        Assertions.assertTrue(resource.exists());

        resource = bfs.getResource("test3.txt");
        Assertions.assertTrue(resource.exists());

        bfs.removeResource("test2.txt");
        bfs.removeResource("test3.txt");

        resource = bfs.getResource("test3.txt");
        Assertions.assertFalse(resource.exists());
    }

    @Test
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
        Assertions.assertTrue(resource.exists());

        resource = bfs.getResource("test5.txt");
        Assertions.assertTrue(resource.exists());

        bfs.removeResource("test4.txt");
        bfs.removeResource("test5.txt");

        resource = bfs.getResource("test5.txt");
        Assertions.assertFalse(resource.exists());
    }

}
