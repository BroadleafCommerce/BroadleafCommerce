package org.broadleafcommerce.cms.admin.server.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jfischer
 */
@Controller
@RequestMapping(value = "/cms.upload.service")
public class UploadController {

    @RequestMapping(method = RequestMethod.POST)
    public String create(UploadMember uploadMember, BindingResult result) {
        if (result.hasErrors()) {
            /*StringBuffer sb = new StringBuffer();
            for (ObjectError error : result.getAllErrors()) {
                sb.append("Error: " + error.getCode() + " - " + error.getDefaultMessage());
            }*/
            return "uploadCompletedView";
        }

        //TODO call the service to complete the static asset upload
        // Some type of file processing...
        System.err.println("-------------------------------------------");
        System.err.println("Test upload: " + uploadMember.getName());
        System.err.println("Test upload: " + uploadMember.getFileData().getOriginalFilename());
        System.err.println("-------------------------------------------");

        return "uploadCompletedView";
    }

}
