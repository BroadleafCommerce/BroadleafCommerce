package org.broadleafcommerce.util;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

public class DependencyLicenseCopy extends Copy {

	protected File licenseDir = null;
	protected Vector filesets = new Vector();
	
	public void execute() throws BuildException {
		super.execute();
		try {
			for (int i = 0; i < filesets.size(); i++) {
			    FileSet fs = (FileSet) filesets.elementAt(i);
			    DirectoryScanner ds = null;
			    try {
			        ds = fs.getDirectoryScanner(getProject());
			    } catch (BuildException e) {
			        if (failonerror
			            || !e.getMessage().endsWith(" not found.")) {
			            throw e;
			        } else {
			            log("Warning: " + e.getMessage());
			            continue;
			        }
			    }
			    String[] srcFiles = ds.getIncludedFiles();
			    for (String file : srcFiles) {
			    	String[] parts = file.split("/");
			    	if (parts.length<=1) {
			    		parts = file.split("\\");
			    	}
			    	if (parts.length <= 1) {
			    		throw new BuildException("Unable to recognize the path separator for src file: " + file);
			    	}
			    	String[] specificParts = new String[parts.length-1];
			    	System.arraycopy(parts, 0, specificParts, 0, specificParts.length);
			    	String specificFilePart = StringUtils.join(specificParts, "/") + "/license.txt";
			    	File specificFile = new File(licenseDir, specificFilePart);
			    	File specificDestinationFile = new File(destDir, specificFilePart);
			    	if (specificFile.exists()) {
			    		fileUtils.copyFile(specificFile, specificDestinationFile);
			    		continue;
			    	}
			    	
			    	String[] generalParts = new String[2];
			    	System.arraycopy(parts, 0, generalParts, 0, 2);
			    	String generalFilePart = StringUtils.join(generalParts, "/") + "/license.txt";
			    	File generalFile = new File(licenseDir, generalFilePart);
			    	if (generalFile.exists()) {
			    		fileUtils.copyFile(generalFile, specificDestinationFile);
			    		continue;
			    	}
			    	
			    	String[] moreGeneralParts = new String[1];
			    	System.arraycopy(parts, 0, moreGeneralParts, 0, 1);
			    	String moreGeneralFilePart = StringUtils.join(moreGeneralParts, "/") + "/license.txt";
			    	File moreGeneralFile = new File(licenseDir, moreGeneralFilePart);
			    	if (moreGeneralFile.exists()) {
			    		fileUtils.copyFile(moreGeneralFile, specificDestinationFile);
			    	}
			    }
			}
		} catch (IOException e) {
			throw new BuildException(e);
		}
    }
	
    public void addFileset(FileSet set) {
    	super.addFileset(set);
        filesets.addElement(set);
    }

	public File getLicenseDir() {
		return licenseDir;
	}

	public void setLicenseDir(File licenseDir) {
		this.licenseDir = licenseDir;
	}
}
