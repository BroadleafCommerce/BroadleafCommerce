/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.artifact.image;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.EffectsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Deflater;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import jakarta.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("blImageArtifactProcessor")
public class ImageArtifactProcessor implements ArtifactProcessor {

    @Resource(name = "blImageEffectsManager")
    protected EffectsManager effectsManager;

    protected String[] supportedUploadTypes = {"gif", "jpg", "jpeg", "png", "bmp", "wbmp"};
    protected float compressionQuality = 0.9F;

    @Value("${image.artifact.recompress.formats:png}")
    protected String recompressFormats = "png";

    @Override
    public boolean isSupported(InputStream artifactStream, String filename) {
        for (String type : supportedUploadTypes) {
            if (filename.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Operation[] buildOperations(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        return effectsManager.buildOperations(parameterMap, artifactStream, mimeType);
    }

    public ImageMetadata getImageMetadata(InputStream artifactStream) throws Exception {
        ImageMetadata imageMetadata = new ImageMetadata();
        ImageInputStream iis = ImageIO.createImageInputStream(artifactStream);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(iis, true);
            imageMetadata.setWidth(reader.getWidth(0));
            imageMetadata.setHeight(reader.getHeight(0));
        } else {
            throw new Exception("Unable to retrieve image metadata from stream. Are you sure the stream provided is a valid input stream for an image source?");
        }

        return imageMetadata;
    }

    @Override
    public InputStream convert(InputStream artifactStream, Operation[] operations, String mimeType) throws Exception {
        if (operations != null && operations.length > 0) {
            ImageInputStream iis = ImageIO.createImageInputStream(artifactStream);
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            ImageReader reader = iter.next();
            String formatName = reader.getFormatName();
            artifactStream.reset();
            BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(artifactStream));

            //before
            if (formatName.toLowerCase().equals("jpeg") || formatName.toLowerCase().equals("jpg")) {
                image = stripAlpha(image);
            }

            for (Operation operation : operations) {
                image = effectsManager.renderEffect(
                        operation.getName(), operation.getFactor(), operation.getParameters(), image, formatName
                );
            }

            if (formatName.toLowerCase().equals("gif")) {
                formatName = "png";
            }
            InputStream result = compress(image, formatName);
            if (formatName.equals("png")) {
                result = compressPNG(result);
            }
            return result;
        } else {
            String[] formats = null;
            if (!StringUtils.isEmpty(recompressFormats)) {
                formats = recompressFormats.split(",");
            }
            //A static PNG asset uploaded by the user will rarely be compressed correctly
            return recompress(artifactStream, formats);
        }
    }

    public InputStream convert(InputStream artifactStream, BufferedImageOp filter) throws Exception {
        ImageInputStream iis = ImageIO.createImageInputStream(artifactStream);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        ImageReader reader = iter.next();
        String formatName = reader.getFormatName();
        artifactStream.reset();
        BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(artifactStream));

        //before
        if (formatName.toLowerCase().equals("jpeg") || formatName.toLowerCase().equals("jpg")) {
            image = stripAlpha(image);
        }

        image = filter.filter(image, null);

        if (formatName.toLowerCase().equals("gif")) {
            formatName = "png";
        }
        InputStream result = compress(image, formatName);
        if (formatName.equals("png")) {
            result = compressPNG(result);
        }
        return result;
    }

    /**
     * Given an input stream on a media file, recompress the file according to best practice optimization standards.
     *
     * @param artifactStream The media input stream
     * @param filterFormats  A list of formats to reduce the scope of influence. When specified, only media matching the
     *                       included formats will be processed. Can be null to cause all formats to be processed.
     * @return
     * @throws Exception
     */
    public InputStream recompress(InputStream artifactStream, String[] filterFormats) throws Exception {
        ByteArrayInputStream inMemoryStream;
        if (artifactStream instanceof ByteArrayInputStream) {
            inMemoryStream = (ByteArrayInputStream) artifactStream;
        } else {
            inMemoryStream = new ByteArrayInputStream(IOUtils.toByteArray(artifactStream));
        }
        ImageInputStream iis = ImageIO.createImageInputStream(inMemoryStream);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        ImageReader reader = iter.next();
        String formatName = reader.getFormatName();
        if (!ArrayUtils.isEmpty(filterFormats)) {
            Arrays.sort(filterFormats);
            int pos = Arrays.binarySearch(filterFormats, formatName.toLowerCase());
            if (pos < 0) {
                return artifactStream;
            }
        }
        inMemoryStream.reset();
        InputStream responseStream;
        if (formatName.toLowerCase().equals("png")) {
            responseStream = compressPNG(inMemoryStream);
        } else {
            BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(inMemoryStream));
            responseStream = compress(image, formatName);
            if (formatName.toLowerCase().equals("gif")) {
                responseStream = compressPNG(responseStream);
            }
        }
        return responseStream;
    }

    protected InputStream compressPNG(InputStream artifactStream) throws Exception {
        PngImage pngImage = new PngImage(artifactStream);
        PngOptimizer optimizer = new PngOptimizer();
        PngImage response = optimizer.optimize(pngImage, true, Deflater.DEFLATED);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.writeDataOutputStream(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    protected InputStream compress(BufferedImage image, String formatName) throws Exception {
        if (formatName.toLowerCase().equals("jpeg") || formatName.toLowerCase().equals("jpg")) {
            image = stripAlpha(image);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(byteArrayOutputStream);
        if (formatName.toLowerCase().equals("gif")) {
            formatName = "png";
        }
        Iterator<ImageWriter> writerIter = ImageIO.getImageWritersByFormatName(formatName);
        ImageWriter writer = writerIter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();

        if (formatName.toLowerCase().equals("jpeg") || formatName.toLowerCase().equals("jpg")) {
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(0.85F);
            iwp.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
        }

        MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(bos);
        writer.setOutput(output);

        IIOImage iomage = new IIOImage(image, null, null);
        writer.write(null, iomage, iwp);
        bos.flush();

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    protected BufferedImage stripAlpha(BufferedImage image) {
        BufferedImage raw_image = image;
        image = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), BufferedImage.TYPE_INT_RGB);
        ColorConvertOp xformOp = new ColorConvertOp(null);
        xformOp.filter(raw_image, image);

        return image;
    }

    public String[] getSupportedUploadTypes() {
        return supportedUploadTypes;
    }

    public void setSupportedUploadTypes(String[] supportedUploadTypes) {
        this.supportedUploadTypes = supportedUploadTypes;
    }

    public float getCompressionQuality() {
        return compressionQuality;
    }

    public void setCompressionQuality(float compressionQuality) {
        this.compressionQuality = compressionQuality;
    }

}
