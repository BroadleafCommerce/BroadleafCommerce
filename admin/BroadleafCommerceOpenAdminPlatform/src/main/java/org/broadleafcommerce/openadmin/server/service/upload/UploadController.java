package org.broadleafcommerce.openadmin.server.service.upload;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jfischer
 */
public class UploadController extends SimpleFormController {

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {

         // cast the bean
        UploadMember bean = (UploadMember) command;

        System.out.println(bean.getFile().getOriginalFilename());
        /*byte[] file = bean.getFile();
        if (file == null) {
             // hmm, that's strange, the user did not upload anything
        }*/

        Map<String, String> model = new HashMap<String, String>();
        model.put("callbackName", bean.getCallbackName());

        return new ModelAndView("uploadCompletedView", model);
    }

    /*protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
        throws ServletException {
        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them
    }*/

}
