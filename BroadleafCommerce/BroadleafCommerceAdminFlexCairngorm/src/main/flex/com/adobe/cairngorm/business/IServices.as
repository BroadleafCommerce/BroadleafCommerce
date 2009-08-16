package com.adobe.cairngorm.business
{
   /**
    * IServices defines an interface for managing services on an IServiceLocator.
    */
   public interface IServices
   {
      /**
       * Register the services.
       * @param serviceLocator the IServiceLocator instance.
       */
      function register( serviceLocator : IServiceLocator ) : void;
      
      /**
       * Return the service with the given name.
       * @param name the name of the service.
       * @return the service.
       */
      function getService( name : String ) : Object;
      
      /**
       * Set the credentials for all registered services.
       * @param username the username to set.
       * @param password the password to set.
       */
      function setCredentials( username : String, password : String ) : void;
      
      /**
       * Set the remote credentials for all registered services.
       * @param username the username to set.
       * @param password the password to set.
       */
      function setRemoteCredentials( username : String, password : String ) : void;
         
      /**
       * Log the user out of all registered services.
       */
      function logout() : void;
   }
}