package org.broadleafcommerce.core.util.service;

import java.util.Map;

public interface DeleteStatementGenerator {
    public Map<String, String> generateDeleteStatementsForType(Class<?> rootType, String rootTypeIdValue);
}
