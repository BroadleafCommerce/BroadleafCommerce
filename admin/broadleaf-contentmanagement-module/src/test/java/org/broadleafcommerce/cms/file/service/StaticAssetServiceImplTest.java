/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.common.file.service.StaticAssetPathServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by bpolster.
 */
public class StaticAssetServiceImplTest {

    @Test
    public void testConvertURLProperties() throws Exception {
        StaticAssetPathServiceImpl staticAssetPathService = new StaticAssetPathServiceImpl();
        staticAssetPathService.setStaticAssetUrlPrefix("cmsstatic");
        staticAssetPathService.setStaticAssetEnvironmentUrlPrefix("http://images.mysite.com/myapp/cmsstatic");

        String url = staticAssetPathService.convertAssetPath("/cmsstatic/product.jpg", "myapp", false);
        Assertions.assertEquals("http://images.mysite.com/myapp/cmsstatic/product.jpg", url);

        staticAssetPathService.setStaticAssetEnvironmentUrlPrefix("http://images.mysite.com");
        url = staticAssetPathService.convertAssetPath("/cmsstatic/product.jpg", "myapp", false);
        Assertions.assertEquals("http://images.mysite.com/product.jpg", url);

        url = staticAssetPathService.convertAssetPath("/cmsstatic/product.jpg", "myapp", true);
        Assertions.assertEquals("https://images.mysite.com/product.jpg", url);


        staticAssetPathService.setStaticAssetEnvironmentUrlPrefix(null);
        url = staticAssetPathService.convertAssetPath("/cmsstatic/product.jpg", "myapp", true);
        Assertions.assertEquals("/myapp/cmsstatic/product.jpg", url);

        url = staticAssetPathService.convertAssetPath("cmsstatic/product.jpg", "myapp", true);
        Assertions.assertEquals("/myapp/cmsstatic/product.jpg", url);
    }

    @Test
    public void testWhenEmptyFileExtensions() throws IOException {
        StaticAssetServiceImpl assetService = new StaticAssetServiceImpl();
        MockMultipartFile file = new MockMultipartFile("text.txt", this.getClass().getResourceAsStream("/testfile/text-file.txt"));
        assetService.validateFileExtension(file);
    }

    @Test
    public void testThrowDisabledFileExtensions() throws IOException {
        StaticAssetServiceImpl assetService = new StaticAssetServiceImpl();
        assetService.setDisabledFileExtensions("txt");
        MockMultipartFile file = new MockMultipartFile("text.txt", this.getClass().getResourceAsStream("/testfile/text-file.txt"));
        assertThrows(
                IOException.class,
                () -> assetService.validateFileExtension(file)
        );
    }

    @Test
    public void testOkWhenMultipleDisableFileExtensions() throws IOException {
        StaticAssetServiceImpl assetService = new StaticAssetServiceImpl();
        assetService.setDisabledFileExtensions("pdf,png");
        MockMultipartFile file = new MockMultipartFile("text.txt", this.getClass().getResourceAsStream("/testfile/text-file.txt"));
        assetService.validateFileExtension(file);
    }

    @Test
    public void testWhitelistFirstDisableFileExtensions() throws IOException {
        StaticAssetServiceImpl assetService = new StaticAssetServiceImpl();
        assetService.setDisabledFileExtensions("pdf,png");
        assetService.setAllowedFileExtensions("txt,png");
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/testfile/text-file.txt");
        MockMultipartFile file = new MockMultipartFile("text.txt", resourceAsStream);
        assetService.validateFileExtension(file);
        file = new MockMultipartFile("img.png", this.getClass().getResourceAsStream("/testfile/img.png"));
        assetService.validateFileExtension(file);
    }

    @Test
    public void testWhitelistFileExtensions() throws IOException {
        StaticAssetServiceImpl assetService = new StaticAssetServiceImpl();
        assetService.setAllowedFileExtensions("txt");
        MockMultipartFile file = new MockMultipartFile("text.txt", this.getClass().getResourceAsStream("/testfile/text-file.txt"));
        assetService.validateFileExtension(file);
        file = new MockMultipartFile("img.png", this.getClass().getResourceAsStream("/testfile/img.png"));
        MockMultipartFile finalFile = file;
        assertThrows(
                IOException.class,
                () -> assetService.validateFileExtension(finalFile)
        );
    }

}
