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

package flex.ant;

import flex.ant.config.ConfigAppendString;
import flex.ant.config.ConfigBoolean;
import flex.ant.config.ConfigInt;
import flex.ant.config.ConfigString;
import flex.ant.config.ConfigVariable;
import flex.ant.config.NestedAttributeElement;
import flex.ant.config.OptionSource;
import flex.ant.config.OptionSpec;
import flex.ant.types.DefaultScriptLimits;
import flex.ant.types.DefaultSize;
import flex.ant.types.FlexFileSet;
import flex.ant.types.Fonts;
import flex.ant.types.Metadata;
import flex.ant.types.RuntimeSharedLibraryPath;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

/**
 *
 */
public final class MxmlcTask extends FlexTask implements DynamicConfigurator
{
    /*=======================================================================*
     *  Static variables and initializer                                     *
     *=======================================================================*/

    private static OptionSpec nsSpec = new OptionSpec("compiler", "namespaces.namespace", "namespace");
    private static OptionSpec liSpec = new OptionSpec("licenses" ,"license");
    private static OptionSpec exSpec = new OptionSpec("externs");
    private static OptionSpec inSpec = new OptionSpec("includes");
    private static OptionSpec irSpec = new OptionSpec(null, "include-resource-bundles", "ir");
    private static OptionSpec rsSpec = new OptionSpec(null, "runtime-shared-libraries", "rsl");
    private static OptionSpec frSpec = new OptionSpec("frames", "frame");

    private static OptionSpec ccSpec = new OptionSpec("compiler", "define");
    private static OptionSpec elSpec = new OptionSpec("compiler", "external-library-path", "el");
    private static OptionSpec ilSpec = new OptionSpec("compiler", "include-libraries");
    private static OptionSpec lpSpec = new OptionSpec("compiler", "library-path", "l");
    private static OptionSpec spSpec = new OptionSpec("compiler", "source-path", "sp");
    private static OptionSpec thSpec = new OptionSpec("compiler", "theme");
    private static OptionSpec lcSpec = new OptionSpec("load-config");
    private static OptionSpec kmSpec = new OptionSpec("compiler", "keep-as3-metadata");

    /*=======================================================================*
     *
     *=======================================================================*/

    private final ArrayList nestedFileSets;

    private Metadata metadata;
    private Fonts fonts;
    private DefaultScriptLimits dLimits;
    private DefaultSize dSize;

    /*=======================================================================*
     * Singular arguments                                                    *
     *=======================================================================*/

    private String file;
    private String output;

    /**
     *
     */
    public MxmlcTask()
    {
        super("mxmlc", "flex2.tools.Compiler", "mxmlc.jar", new ConfigVariable[] {
            //Basic Booleans
            new ConfigBoolean(new OptionSpec("benchmark")),
            new ConfigBoolean(new OptionSpec("compiler", "accessible")),
            new ConfigBoolean(new OptionSpec("compiler", "debug")),
            new ConfigBoolean(new OptionSpec("compiler", "incremental")),
            new ConfigBoolean(new OptionSpec("compiler", "optimize")),
            new ConfigBoolean(new OptionSpec("compiler", "show-actionscript-warnings")),
            new ConfigBoolean(new OptionSpec("compiler", "show-binding-warnings")),
            new ConfigBoolean(new OptionSpec("compiler", "show-deprecation-warnings")),
            new ConfigBoolean(new OptionSpec("compiler", "show-unused-type-selector-warnings")),
            new ConfigBoolean(new OptionSpec("compiler", "strict")),
            new ConfigBoolean(new OptionSpec("compiler", "use-resource-bundle-metadata")),
            new ConfigBoolean(new OptionSpec("use-network")),
            new ConfigBoolean(new OptionSpec("warnings")),
            //Advanced Booleans
            new ConfigBoolean(new OptionSpec("compiler", "allow-source-path-overlap")),
            new ConfigBoolean(new OptionSpec("compiler", "as3")),
            new ConfigBoolean(new OptionSpec("compiler", "doc")),
            new ConfigBoolean(new OptionSpec("compiler", "es")),
            new ConfigBoolean(new OptionSpec("compiler", "headless-server")),
            new ConfigBoolean(new OptionSpec("compiler", "keep-all-type-selectors")),
            new ConfigBoolean(new OptionSpec("compiler", "keep-generated-actionscript", "keep")),
            new ConfigBoolean(new OptionSpec("compiler", "verbose-stacktraces")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-array-tostring-changes")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-assignment-within-conditional")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-array-cast")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-bool-assignment")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-date-cast")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-es3-type-method")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-es3-type-prop")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-nan-comparison")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-null-assignment")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-null-comparison")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-bad-undefined-comparison")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-boolean-constructor-with-no-args")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-changes-in-resolve")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-class-is-sealed")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-const-not-initialized")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-constructor-returns-value")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-deprecated-event-handler-error")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-deprecated-function-error")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-deprecated-property-error")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-duplicate-argument-names")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-duplicate-variable-def")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-for-var-in-changes")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-import-hides-classes")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-instance-of-changes")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-internal-error")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-level-not-supported")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-missing-namespace-decl")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-negative-uint-literal")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-no-constructor")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-no-explicit-super-call-in-constructor")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-no-type-decl")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-number-from-string-changes")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-scoping-change-in-this")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-slow-text-field-addition")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-unlikely-function-value")),
            new ConfigBoolean(new OptionSpec("compiler", "warn-xml-class-has-changed")),
            new ConfigBoolean(new OptionSpec(null, "static-link-runtime-shared-libraries", "static-rsls")),
            //String Variables
            new ConfigString(new OptionSpec("compiler", "actionscript-file-encoding")),
            new ConfigString(new OptionSpec("compiler", "mxml.compatibility-version", "compatibility-version")),
            new ConfigString(new OptionSpec("compiler", "context-root")),
            new ConfigString(new OptionSpec("compiler", "defaults-css-url")),
            new ConfigString(new OptionSpec("compiler", "locale")),
            new ConfigString(new OptionSpec("compiler", "services")),
            new ConfigString(new OptionSpec("debug-password")),
            new ConfigString(new OptionSpec("dump-config")),
            new ConfigString(new OptionSpec("link-report")),
            new ConfigString(new OptionSpec("load-externs")),
            new ConfigString(new OptionSpec(null, "output", "o")),
            new ConfigString(new OptionSpec("raw-metadata")),
            new ConfigString(new OptionSpec("resource-bundle-list")),
            new ConfigString(new OptionSpec("target-player")),
            new ConfigAppendString(new OptionSpec("configname")),
            //Int Variables
            new ConfigInt(new OptionSpec("default-background-color")),
            new ConfigInt(new OptionSpec("default-frame-rate"))
        });
        
        nestedAttribs = new ArrayList();
        nestedFileSets = new ArrayList();
    }

    /*=======================================================================*
     * Required Attributes                                                   *
     *=======================================================================*/

    public void setFile(String file)
    {
        this.file = file;
    }
    
    /*=======================================================================*
     * Other Attributes                                                      *
     *=======================================================================*/
    
    /*
     * Necessary to override inherited setOutput method since ant gives
     * priority to parameter types more specific than String.
     */
    public void setOutput(File o){
        setOutput(o.getAbsolutePath());
    }
    
    public void setOutput(String o){
        this.output = o;
    }

    /*=======================================================================*
     *  Child Elements                                                       *
     *=======================================================================*/

    public Metadata createMetadata() 
    {
        if (metadata == null)
            return metadata = new Metadata();
        else
            throw new BuildException("Only one nested <metadata> element is allowed in an <mxmlc> task.");
    }

    public Fonts createFonts()
    {
        if (fonts == null)
            return fonts = new Fonts();
        else
            throw new BuildException("Only one nested <fonts> element is allowed in an <mxmlc> task.");
    }

    public NestedAttributeElement createNamespace()
    {
        return createElem(new String[] { "uri", "manifest" }, nsSpec);
    }

    public NestedAttributeElement createLicense()
    {
        return createElem(new String[] { "product", "serial-number" }, liSpec);
    }

    public NestedAttributeElement createExterns()
    {
        return createElem("symbol", exSpec);
    }

    public NestedAttributeElement createIncludes()
    {
        return createElem("symbol", inSpec);
    }

    public NestedAttributeElement createFrame()
    {
        return createElem(new String[] { "label", "classname" }, frSpec);
    }

    public Object createDynamicElement(String name)
    {
        if (kmSpec.matches(name)) {
            return createElem("name", kmSpec);            
        }
        else if (ccSpec.matches(name)) {
            return createElem(new String[] { "name", "value" }, ccSpec);
        }
        else if (rsSpec.matches(name)) {
            return createElem("url", rsSpec);
        }
        else if (rslpSpec.matches(name)) {
            RuntimeSharedLibraryPath runtimeSharedLibraryPath = new RuntimeSharedLibraryPath();
            nestedAttribs.add(runtimeSharedLibraryPath);
            return runtimeSharedLibraryPath;
        }
        else if (lcSpec.matches(name)) {
            return createElem("filename", lcSpec);
        }
        else if (spSpec.matches(name)) {
            return createElem("path-element", spSpec);
        }
        else if (DefaultScriptLimits.spec.matches(name)) {
            if (dLimits == null)
                return dLimits = new DefaultScriptLimits();
            else
                throw new BuildException("Only one nested <default-script-limits> element is allowed in an <mxmlc> task.");
        }
        else if (DefaultSize.spec.matches(name)) {
            if (dSize == null)
                return dSize = new DefaultSize();
            else
                throw new BuildException("Only one nested <default-size> element is allowed in an <mxmlc> task.");
        }
        else if (elSpec.matches(name)) {
            FlexFileSet fs = new FlexFileSet(elSpec, true);
            nestedFileSets.add(fs);
            return fs;
        }
        else if (ilSpec.matches(name)) {
            FlexFileSet fs = new FlexFileSet(ilSpec, true);
            nestedFileSets.add(fs);
            return fs;
        }
        else if (lpSpec.matches(name)) {
            FlexFileSet fs = new FlexFileSet(lpSpec, true);
            nestedFileSets.add(fs);
            return fs;
        }
        else if (thSpec.matches(name)) {
            FlexFileSet fs = new FlexFileSet(thSpec);
            nestedFileSets.add(fs);
            return fs;
        }
        else if (irSpec.matches(name)) {
            return createElem("bundle", irSpec);
        }

        return super.createDynamicElement(name);
    }

    /*=======================================================================*
     *  Execute and Related Functions                                        *
     *=======================================================================*/

    protected void prepareCommandline() throws BuildException
    {
        for (int i = 0; i < variables.length; i++) {
            variables[i].addToCommandline(cmdl);
        }

        if (metadata != null)
            metadata.addToCommandline(cmdl);

        if(fonts != null)
            fonts.addToCommandline(cmdl);

        if (dLimits != null)
            dLimits.addToCommandline(cmdl);

        if (dSize != null)
            dSize.addToCommandline(cmdl);

        Iterator it = nestedAttribs.iterator();

        while (it.hasNext())
            ((OptionSource) it.next()).addToCommandline(cmdl);

        it = nestedFileSets.iterator();

        while (it.hasNext())
            ((OptionSource) it.next()).addToCommandline(cmdl);
        
        if(output != null)
        {
            // see FlexTask.execute() for explanation
            final String tmpOutput = (fork ? "\"" + output + "\"" : output);
            (new ConfigString(new OptionSpec(null, "output", "o"), tmpOutput)).addToCommandline(cmdl);
        }
        
        // end of arguments
        cmdl.createArgument().setValue("--");

        // file-spec may not be specified if building, e.g. a resource bundle SWF
        if (file != null)
        {
            // see FlexTask.execute() for explanation
            final String tmpFile = (fork ? "\"" + file + "\"" : file);
            cmdl.createArgument().setValue(tmpFile);
        }
    }

} //End of MxmlcTask
