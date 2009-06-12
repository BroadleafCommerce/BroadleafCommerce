////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex.ant.types;

import flex.ant.config.OptionSource;
import flex.ant.config.OptionSpec;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;

/**
 *
 */
public class FlexFileSet extends FileSet implements OptionSource
{
    protected final OptionSpec spec;
    protected final boolean includeDirs;

    protected boolean append;

    public FlexFileSet()
    {
        this(false);
    }

    public FlexFileSet(OptionSpec spec)
    {
        this(spec, false);
    }

    public FlexFileSet(boolean dirs)
    {
        spec = null;
        includeDirs = dirs;
        append = false;
    }

    public FlexFileSet(OptionSpec spec, boolean dirs)
    {
        this.spec = spec; 
        includeDirs = dirs;
        append = false;
    }

    public void setAppend(boolean value)
    {
        append = value;
    }

    public void addToCommandline(Commandline cmdl)
    {
        if (hasSelectors() || hasPatterns())
        {
            DirectoryScanner scanner = getDirectoryScanner(getProject());

            if (includeDirs)
            {
                addFiles(scanner.getBasedir(), scanner.getIncludedDirectories(), cmdl);
            }

            addFiles(scanner.getBasedir(), scanner.getIncludedFiles(), cmdl);
        }
        else if (spec != null)
        {
            cmdl.createArgument().setValue("-" + spec.getFullName() + "=");
        }
    }

    private void addFiles(File base, String[] files, Commandline cmdl)
    {
        FileUtils utils = FileUtils.newFileUtils();

        if (spec == null)
        {
            for (int i = 0; i < files.length; i++)
            {
                cmdl.createArgument().setValue(utils.resolveFile(base, files[i]).getAbsolutePath());
            }
        }
        else
        {
            for (int i = 0; i < files.length; i++)
            {
                cmdl.createArgument().setValue("-" + spec.getFullName() + equalString() +
                                               utils.resolveFile(base, files[i]).getAbsolutePath());
            }
        }
    }

    private String equalString()
    {
        return append ? "+=" : "=";
    }
} //End of FlexFileSet
