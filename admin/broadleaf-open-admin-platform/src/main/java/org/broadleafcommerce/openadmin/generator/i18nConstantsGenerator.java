/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.generator;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class i18nConstantsGenerator extends Generator {

    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        JClassType clazz = typeOracle.findType(typeName);
        if (clazz == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
            + typeName + "'", null);
            throw new UnableToCompleteException();
        }
        try {
            Map<String, String> generatedClasses = generateDynamicConstantClasses(clazz, logger, context);
            logger.log(TreeLogger.INFO, "Generating source for " + clazz.getQualifiedSourceName(), null);

            String packageName = clazz.getPackage().getName();
            String simpleName = clazz.getName() + "Wrapper";
            SourceWriter sourceWriter = getSourceWriter(packageName, simpleName, context, logger, new String[]{"org.broadleafcommerce.openadmin.client.i18nConstants"});
            if (sourceWriter != null) {
                sourceWriter.println("private java.util.List<String> supportedLocales = new java.util.ArrayList<String>();");
                sourceWriter.println("public " + simpleName + "() {");
                for (Map.Entry<String, String> entry : generatedClasses.entrySet()) {
                    sourceWriter.print("supportedLocales.add(\"");
                    sourceWriter.print(entry.getKey());
                    sourceWriter.print("\");\n");
                }
                sourceWriter.println("}");
                sourceWriter.println("");
                sourceWriter.println("public void retrievei18nProperties(final org.broadleafcommerce.openadmin.client.i18nPropertiesClient i18nClient) {");
                sourceWriter.println("com.google.gwt.i18n.client.LocaleInfo localeInfo = com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale();");
                sourceWriter.println("String localeName = localeInfo.getLocaleName();");
                sourceWriter.println("if (!supportedLocales.contains(localeName) && localeName.contains(\"_\")){");
                //look for a language equiv
                sourceWriter.println("localeName = localeName.substring(0, localeName.indexOf(\"_\"));");
                sourceWriter.println("if (!supportedLocales.contains(localeName)){");
                sourceWriter.println("localeName = null;");
                sourceWriter.println("}");
                sourceWriter.println("} else {");
                sourceWriter.println("localeName = null;");
                sourceWriter.println("}");
                sourceWriter.println("if (localeName == null){");
                //TODO re-introduce runAsync to optimize loading of i18n properties. This currently doesn't work because of timing problems with the module loading
                //sourceWriter.println("com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() {");
                //sourceWriter.println("public void onFailure(Throwable err) {");
                //sourceWriter.println("asyncClient.onUnavailable(err);");
                //sourceWriter.println("}");
                //sourceWriter.println("public void onSuccess() {");
                sourceWriter.print("i18nClient.onSuccess(new ");
                sourceWriter.print(generatedClasses.values().iterator().next());
                sourceWriter.print("().getAlli18nProperties());\n");
                //sourceWriter.println("}});");
                sourceWriter.println("return;");
                sourceWriter.println("}");
                for (Map.Entry<String, String> entry : generatedClasses.entrySet()) {
                    sourceWriter.println("if (localeName.equals(\""+entry.getKey()+"\")){");
                    //sourceWriter.println("com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() {");
                    //sourceWriter.println("public void onFailure(Throwable err) {");
                    //sourceWriter.println("asyncClient.onUnavailable(err);");
                    //sourceWriter.println("}");
                    //sourceWriter.println("public void onSuccess() {");
                    sourceWriter.print("i18nClient.onSuccess(new ");
                    sourceWriter.print(entry.getValue());
                    sourceWriter.print("().getAlli18nProperties());\n");
                    //sourceWriter.println("}});");
                    sourceWriter.println("return;");
                    sourceWriter.println("}");
                }
                sourceWriter.println("i18nClient.onUnavailable(new RuntimeException(\"Unable to find a localized file for "+packageName+"."+simpleName+"\"));");
                sourceWriter.println("}");
                sourceWriter.commit(logger);
                logger.log(TreeLogger.INFO, "Done Generating source for "
                + clazz.getQualifiedSourceName() + "Wrapper", null);
            }

            return clazz.getQualifiedSourceName() + "Wrapper";
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new UnableToCompleteException();
        }
    }

    protected Map<String, String> generateDynamicConstantClasses(JClassType clazz, TreeLogger logger, GeneratorContext context) throws NotFoundException {
        ResourceOracle resourceOracle = context.getResourcesOracle();
        Map<String, String> generatedClassses = new HashMap<String, String>();
        String myTypeName = clazz.getQualifiedSourceName().replace('.','/');
        Map<String, Resource> resourceMap = resourceOracle.getResourceMap();
        for (Map.Entry<String, Resource> entry : resourceMap.entrySet()) {
            if (entry.getKey().contains(myTypeName) && entry.getKey().endsWith(".properties")) {
                String noSuffix = entry.getKey().substring(0, entry.getKey().indexOf(".properties"));
                String position1 = null;
                String position2 = null;
                if (noSuffix.contains("_")) {
                    String i18nMatch = noSuffix.substring(noSuffix.lastIndexOf("_") + 1, noSuffix.length());
                    if (i18nMatch != null && i18nMatch.length() == 2) {
                        position1 = i18nMatch;
                        noSuffix = noSuffix.substring(0, noSuffix.lastIndexOf("_"));
                        if (noSuffix.contains("_")) {
                            i18nMatch = noSuffix.substring(noSuffix.lastIndexOf("_") + 1, noSuffix.length());
                            if (i18nMatch != null && i18nMatch.length() == 2) {
                                position2 = i18nMatch;
                            }
                        }
                    }
                }
                String packageName = clazz.getPackage().getName();
                StringBuilder suffix = new StringBuilder();
                if (position1 != null) {
                    suffix.append("_");
                    suffix.append(position1);
                }
                if (position2 != null) {
                    suffix.append("_");
                    suffix.append(position2);
                }
                if (position1 == null && position2 == null) {
                    suffix.append("_default");
                }
                String simpleName = clazz.getName() + suffix.toString();
                SourceWriter sourceWriter = getSourceWriter(packageName, simpleName, context, logger, new String[]{});
                if (sourceWriter != null) {
                    Map<String, String> props = new HashMap<String, String>();
                    InputStream is = entry.getValue().openContents();
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        boolean eof = false;
                        while (!eof) {
                            String temp = in.readLine();
                            if (temp == null) {
                                eof = true;
                            } else {
                                temp = temp.trim();
                                if (!temp.startsWith("#") && temp.length() > 0 && temp.contains("=")) {
                                    String key = temp.substring(0, temp.indexOf("=")).trim();
                                    String value = temp.substring(temp.indexOf("=")+1, temp.length()).trim();
                                    props.put(key,value);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            is.close();
                        } catch (Throwable ignored) {
                        }
                    }

                    logger.log(TreeLogger.INFO, "Emitting localized code for: " + entry.getKey(), null);
                    sourceWriter.println("private java.util.Map<String, String> i18nProperties = new java.util.HashMap<String, String>();");
                    sourceWriter.println("public " + simpleName + "() {");
                    for (Map.Entry<String, String> prop : props.entrySet()) {
                        sourceWriter.print("i18nProperties.put(\"");
                        sourceWriter.print(prop.getKey());
                        sourceWriter.print("\",\"");
                        sourceWriter.print(prop.getValue().replace("\"", "\\\""));
                        sourceWriter.print("\");\n");
                    }
                    sourceWriter.println("}");
                    sourceWriter.println("");
                    sourceWriter.println("public java.util.Map<String, String> getAlli18nProperties() {");
                    sourceWriter.println("return i18nProperties;");
                    sourceWriter.println("}");
                    sourceWriter.commit(logger);
                    logger.log(TreeLogger.INFO, "Done Generating source for "
                    + packageName + "." + simpleName, null);

                    generatedClassses.put(suffix.toString().substring(1, suffix.toString().length()), packageName + "." + simpleName);
                }
            }
        }

        return generatedClassses;
    }

    public SourceWriter getSourceWriter(String packageName, String simpleName, GeneratorContext context, TreeLogger logger, String[] interfaces) {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
        for (String interfaceItem : interfaces) {
            composer.addImplementedInterface(interfaceItem);
        }
        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        } else {
            SourceWriter sw = composer.createSourceWriter(context, printWriter);
            return sw;
        }
    }
}
