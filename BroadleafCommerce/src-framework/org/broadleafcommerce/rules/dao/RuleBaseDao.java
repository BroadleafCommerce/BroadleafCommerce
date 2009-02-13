package org.broadleafcommerce.rules.dao;

import java.util.List;

import org.drools.rule.Package;

public interface RuleBaseDao {

    public List<Package> getRulePackages();
}
