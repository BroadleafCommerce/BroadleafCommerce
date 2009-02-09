package org.broadleafcommerce.rules.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class RuleDeployer {

	private RuleBaseService ruleBaseService;

	public void setRuleBaseService(RuleBaseService ruleBaseService) {
		this.ruleBaseService = ruleBaseService;
	}

	public Package addRuleToPackage(File drlFile){

		PackageBuilder pkgBuilder = new PackageBuilder();

		Reader drlFileReader = null;;

		try {
			drlFileReader = new FileReader(drlFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			pkgBuilder.addPackageFromDrl(drlFileReader);
		} catch (DroolsParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Package pkg = pkgBuilder.getPackage();

		return pkg;

	}

	public void mergePackageWithRuleBase(Package pkg) {

		try {
			ruleBaseService.getRuleBase().addPackage(pkg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
