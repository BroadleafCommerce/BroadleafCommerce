package org.broadleafcommerce.email.service.message;

public enum EmailPropertyType {
    USER{
        @Override
        public String toString() {
            return "user";
        }
    },
    INFO{
        @Override
        public String toString() {
            return "info";
        }
    },
    SERVERINFO {
        @Override
        public String toString() {
            return "serverInfo";
        }
    }
}
