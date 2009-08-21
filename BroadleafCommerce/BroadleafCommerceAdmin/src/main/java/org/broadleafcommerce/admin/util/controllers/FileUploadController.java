package org.broadleafcommerce.admin.util.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.admin.util.domain.FileUploadBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.BindException;


public class FileUploadController extends SimpleFormController {

    protected ModelAndView onSubmit(
            HttpServletRequest request,
            HttpServletResponse response,
            Object command,
            BindException errors) throws ServletException, IOException {

             // cast the bean
            FileUploadBean bean = (FileUploadBean) command;

            // let's see if there's content there
            MultipartFile file = bean.getFile();
            if (file == null) {
                 // hmm, that's strange, the user did not upload anything
            }

            try {
                String uriBegining = request.getRequestURI().substring(1,request.getRequestURI().indexOf("/",2));
                String basepath = request.getPathTranslated().substring(0,request.getPathTranslated().indexOf(uriBegining)+uriBegining.length());
                String absoluteFilename = basepath+File.separator+bean.getDirectory()+File.separator+file.getOriginalFilename();
                FileSystemResource fileResource = new FileSystemResource(absoluteFilename);

                checkDirectory(basepath+bean.getDirectory());

                backupExistingFile(fileResource, basepath+bean.getDirectory());
                
                
                FileOutputStream fout = new FileOutputStream(new FileSystemResource(basepath+File.separator+bean.getDirectory()+File.separator+file.getOriginalFilename()).getFile());
                BufferedOutputStream bout = new BufferedOutputStream(fout);
                BufferedInputStream bin = new BufferedInputStream(file.getInputStream());
                int x;
                while((x = bin.read()) != -1) {
                    bout.write(x);
                }
                bout.flush();
                bout.close();
                bin.close();
                return super.onSubmit(request, response, command, errors);                
            }catch(Exception e) {
                //Exception occured;
                e.printStackTrace();
                throw new RuntimeException(e);
                // return null;                
            }
        }
    
    private void checkDirectory(String basepath) {
        FileSystemResource dirResource = new FileSystemResource(basepath);
        if(!dirResource.exists()) {
            File f = new File(basepath);
            if(!f.mkdirs()){
                throw new RuntimeException("Could not create directories "+f.getAbsolutePath());
            }
            
        }
    }
    
    private void backupExistingFile(FileSystemResource fileResource, String basepath) {
        if(fileResource.exists()) {
            String originalFilename = fileResource.getFilename();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            int dotIndex = originalFilename.lastIndexOf(".");
            String extension = originalFilename.substring(dotIndex, originalFilename.length());
            String filename = originalFilename.substring(0,dotIndex);
            String dateString = dateFormat.format(new Date());
            String backupFilename = filename+dateString+extension;
            fileResource.getFile().renameTo(new File(basepath+File.separator+backupFilename));
        }        
    }
}
