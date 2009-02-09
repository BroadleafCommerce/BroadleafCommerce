package org.broadleafcommerce.rules.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;

import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.drools.rule.Package;

public class FileHelper {

	private RuleDeployer ruleDeployer;

	public void setRuleBaseService(RuleDeployer ruleDeployer){
		this.ruleDeployer = ruleDeployer;
	}

	public void writeRuleFile(ShoppingCartPromotion shoppingCartPromotion) {

		try {

			File drlFile = new File("war/WEB-INF/drools/"
					+ shoppingCartPromotion.getName() + ".drl");

			Writer output = new BufferedWriter(new FileWriter(drlFile));

			if (drlFile == null) {
				throw new IllegalArgumentException("File should not be null.");
			}
			if (!drlFile.exists()) {
				throw new FileNotFoundException("File does not exist: " + drlFile);
			}
			if (!drlFile.isFile()) {
				throw new IllegalArgumentException(
						"Should not be a directory: " + drlFile);
			}
			if (!drlFile.canWrite()) {
				throw new IllegalArgumentException("File cannot be written: "
						+ drlFile);
			}

			String newLine = "\n";
			String tab = "\t";

			output.write("rule \"" + shoppingCartPromotion.getName() + "\""
					+ newLine);

			output.write("when" + newLine + tab);

			if (!shoppingCartPromotion.getCouponCode().isEmpty()) {
				output.write("CouponCode(couponCode == \""
						+ shoppingCartPromotion.getCouponCode() + "\")"
						+ newLine);
			}

			output.write("then" + newLine + tab);

			output.write("System.out.println(\"SUCCESS\");" + newLine);

			output.write("end");

			output.close();

			Package pkg = ruleDeployer.addRuleToPackage(drlFile);

			ruleDeployer.mergePackageWithRuleBase(pkg);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error writing drools file");
		} finally {

		}


	}

}
