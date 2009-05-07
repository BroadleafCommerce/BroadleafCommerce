package org.broadleafcommerce.email.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.email.domain.AbstractEmailTarget;
import org.broadleafcommerce.email.domain.EmailListType;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.broadleafcommerce.email.service.validator.EmailListRequest;
import org.broadleafcommerce.util.WebserviceConsumer;

public class EmailListServiceImpl implements EmailListService, WebserviceConsumer {
    private final Log logger = LogFactory.getLog(getClass());
    private final EmailService emailService;

    private String webserviceUrl;
    private int webserviceTimeout = 180000;
    private String emailProperties;
    private String welcomeEmailProperties;

    public EmailListServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    public String getWebserviceUrl() {
        return webserviceUrl;
    }

    public void setWebserviceUrl(String webserviceUrl) {
        this.webserviceUrl = webserviceUrl;
    }

    public int getWebserviceTimeout() {
        return webserviceTimeout;
    }

    public void setWebserviceTimeout(int webserviceTimeout) {
        this.webserviceTimeout = webserviceTimeout;
    }

    public String getEmailProperties() {
        return emailProperties;
    }

    public void setEmailProperties(String emailProperties) {
        this.emailProperties = emailProperties;
    }

    public String getWelcomeEmailProperties() {
        return welcomeEmailProperties;
    }

    public void setWelcomeEmailProperties(String welcomeEmailProperties) {
        this.welcomeEmailProperties = welcomeEmailProperties;
    }

    @Override
    public void changeEmailAddress(String oldEmail, String newEmail) {
        // TODO complete this service if it is needed for how it was used with the UserFormHandler, otherwise delete
    }

    @Override
    public boolean emailExists(String email) {
        boolean exists = false;

        try {
            Call call = generateCall();
            // Create Call object and set parameters
            call.setOperationName(new QName("EmailManager", "emailExists"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress

            call.setReturnType(XMLType.XSD_BOOLEAN);
            call.setTimeout(new Integer(180000));

            Object[] args = new Object[] { email };
            Boolean result = (Boolean) call.invoke(args);

            exists = result.booleanValue();

            logger.debug("emailExists() => Result: " + exists);

        } catch (javax.xml.rpc.ServiceException se) {
            logger.error("emailExists () => ", se);
        } catch (java.net.MalformedURLException meu) {
            logger.error("emailExists () => ", meu);
        } catch (java.rmi.RemoteException re) {
            logger.error("emailExists () => ", re);
        }

        return exists;
    }

    @Override
    public void sendWelcomeEmail(String emailAddress) {
        AbstractEmailTarget emailUser = new AbstractEmailTarget(){}; //TODO scc: should this be anonymous class?
        emailUser.setEmailAddress(emailAddress);
        EmailInfo info = null;
        try {
            info = new EmailInfo(new String[]{emailProperties, welcomeEmailProperties});
        } catch (IOException e) {
            // this should only occur if the properties file doesn't exist
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", emailUser);
        map.put("info", info);

        emailService.sendTemplateEmail(map);
    }

    @Override
    public void setCustomerStore(String email, String store) {
        try {
            // Create Call object and set parameters
            Call call = generateCall();
            call
            .setOperationName(new QName("EmailManager",
            "setCustomerStore"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress
            call.addParameter("in1", XMLType.XSD_STRING, ParameterMode.IN); // store

            call.setReturnType(XMLType.XSD_STRING);
            call.setTimeout(new Integer(180000));

            String result = (String) call.invoke(new Object[] { email, store });

            logger.debug("setCustomerStore() => Result: " + result);

        } catch (javax.xml.rpc.ServiceException se) {
            logger.error("setCustomerStore () => Error setting for for '"
                    + email + "': ", se);
        } catch (java.net.MalformedURLException meu) {
            logger.error("setCustomerStore () => Error setting for for '"
                    + email + "': ", meu);
        } catch (java.rmi.RemoteException re) {
            logger.error("setCustomerStore () => Error setting for for '"
                    + email + "': ", re);
        }
    }

    public EmailListType retrieveCurrentList(String emailAddress) {
        // Check to see if the user is already subscribed
        boolean subscribedToMaster = isOnList(emailAddress, EmailListType.MASTER);
        boolean subscribedToMonthly = isOnList(emailAddress, EmailListType.MONTHLY);

        EmailListType currentList = EmailListType.NONE;
        if ( subscribedToMaster ) {
            currentList = EmailListType.MASTER;
        }
        else if ( subscribedToMonthly ) {
            currentList = EmailListType.MONTHLY;
        }
        return currentList;
    }

    @Override
    public boolean isOnList(String emailAddress, EmailListType emailListType) {
        boolean isOnList = false;

        try {
            // Create Call object and set parameters
            Call call = generateCall();
            call.setOperationName(new QName("EmailManager", "isOnList"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress
            call.addParameter("in1", XMLType.XSD_STRING, ParameterMode.IN); // listId

            call.setReturnType(XMLType.XSD_BOOLEAN);
            call.setTimeout(new Integer(180000));

            Object[] args = new Object[] {
                    emailAddress,
                    emailListType.name()
            };

            Boolean result = (Boolean) call.invoke(args);

            isOnList = result.booleanValue();
            logger.debug("isOnList() => Result: " + isOnList);
        } catch (javax.xml.rpc.ServiceException se) {
            logger.error("isOnList () => ", se);
        } catch (java.net.MalformedURLException meu) {
            logger.error("isOnList () => ", meu);
        } catch (java.rmi.RemoteException re) {
            logger.error("isOnList () => ", re);
        }
        return isOnList;
    }

    //@Override
    public void subscribe(EmailListRequest emailListRequest) {
        if(EmailListType.NONE.equals(emailListRequest.getEmailListType())) {
            return;
        }
        String profileId = null;
        String closestStore = null;

        try {
            // Create Call object and set parameters
            Call call = generateCall();
            call.setOperationName(new QName("EmailManager",	"addEmailCustomer"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress
            call.addParameter("in1", XMLType.XSD_STRING, ParameterMode.IN); // source
            call.addParameter("in2", XMLType.XSD_STRING, ParameterMode.IN); // sourceCustId
            call.addParameter("in3", XMLType.XSD_STRING, ParameterMode.IN); // emailUsage
            call.addParameter("in4", XMLType.XSD_STRING, ParameterMode.IN); // comment
            call.addParameter("in5", XMLType.XSD_STRING, ParameterMode.IN); // listId
            call.setReturnType(XMLType.XSD_STRING);

            Object[] args = new Object[] { emailListRequest.getEmailAddress(),
                    "web", profileId, // could be null
                    "Y", // "Y" = ok to email
                    emailListRequest.getComment(), // could be null - used to
                    // record browser, ip etc.
                    emailListRequest.getEmailListType().name()};

            String result = (String) call.invoke(args);
            if (emailListRequest.isSendConfirmationEmail()) {
                sendWelcomeEmail(emailListRequest.getEmailAddress());
            }

            logger.debug("subscribe() => Result: " + result);

            if (closestStore != null) {
                //TODO: setCustomerStore(email, closestStore);
            }

        } catch (RemoteException e) {
            logger.error("subscribe () => Error subscribing '" + emailListRequest.getEmailAddress() + "': ", e);
        } catch (MalformedURLException e) {
            logger.error("subscribe () => Error subscribing '" + emailListRequest.getEmailAddress() + "': ", e);
        } catch (ServiceException e) {
            logger.error("subscribe () => Error subscribing '" + emailListRequest.getEmailAddress() + "': ", e);
        }

    }

    @Override
    public void unsubscribe(String email) {
        try {
            // Create Call object and set parameters
            Call call = generateCall();
            call.setOperationName(new QName("EmailManager", "unsubscribeAll"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress
            call.addParameter("in1", XMLType.XSD_BOOLEAN, ParameterMode.IN); // sendToCheetah

            call.setReturnType(XMLType.XSD_STRING);
            call.setTimeout(new Integer(180000));

            Object[] args = new Object[] { email, Boolean.TRUE };

            String result = (String) call.invoke(args);
            logger.info("unsubscribe() => Result from web service: " + result);

        } catch (javax.xml.rpc.ServiceException se) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "': ", se);
        } catch (java.net.MalformedURLException meu) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "': ", meu);
        } catch (java.rmi.RemoteException re) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "': ", re);
        }
    }

    @Override
    public void unsubscribe(String email, EmailListType emailListType) {
        try {

            // Create Call object and set parameters
            Call call = generateCall();
            call.setOperationName(new QName("EmailManager", "unsubscribe"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN); // emailAddress
            call.addParameter("in1", XMLType.XSD_STRING, ParameterMode.IN); // listId
            call.addParameter("in2", XMLType.XSD_BOOLEAN, ParameterMode.IN); // sendToCheetah

            call.setReturnType(XMLType.XSD_STRING);

            Object[] args = new Object[] { email, emailListType.name(), Boolean.TRUE };

            String result = (String) call.invoke(args);
            logger.debug("unsubscribe() => Result from web service: " + result);

        } catch (javax.xml.rpc.ServiceException se) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "' from '" + emailListType.name() + "': ", se);
        } catch (java.net.MalformedURLException meu) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "' from '" + emailListType.name() + "': ", meu);
        } catch (java.rmi.RemoteException re) {
            logger.error("unsubscribe () => Error unsubscribing '" + email
                    + "' from '" + emailListType.name() + "': ", re);
        }
    }

    private Call generateCall() throws ServiceException, MalformedURLException {
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new URL(webserviceUrl));
        call.setTimeout(webserviceTimeout);
        return call;
    }
}
