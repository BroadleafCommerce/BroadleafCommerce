/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

/**
 * 
 * @author jfischer
 *
 */
public class FactoryGenerator extends Generator {

    public static final String INSTANTIABLE_TYPE = "org.broadleafcommerce.openadmin.client.reflection.Instantiable";
    public static final String VALIDATOR_TYPE = "com.smartgwt.client.widgets.form.validator.Validator";
    
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        logger.log(TreeLogger.INFO, "Generating source for " + typeName, null);
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType clazz = typeOracle.findType(typeName);
        if (clazz == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
            + typeName + "'", null);
            throw new UnableToCompleteException();
        }
        try {
            logger.log(TreeLogger.INFO, "Generating source for " + clazz.getQualifiedSourceName(), null);
            JClassType[] reflectableTypes = {typeOracle.getType(INSTANTIABLE_TYPE)};
            JClassType[] validatorTypes = {typeOracle.getType(VALIDATOR_TYPE)};
            SourceWriter sourceWriter = getSourceWriter(clazz, context, logger);
            if (sourceWriter != null) {
                sourceWriter.println("public java.lang.Object newInstance(String className) {");
                for (JClassType validatorType : validatorTypes) {
                    JClassType[] types = typeOracle.getTypes();
                    int count = 0;
                    for (int i = 0; i < types.length; i++) {
                        if (types[i].isInterface() == null
                        && !types[i].isAbstract()
                        && types[i].isAssignableTo(validatorType)) {
                            logger.log(TreeLogger.INFO, "Emitting instantiation code for: " + types[i].getQualifiedSourceName(), null);
                            if (count == 0) {
                                sourceWriter.println("   if(\""
                                + types[i].getQualifiedSourceName()
                                + "\".equals(className)) {"
                                + " return new "
                                + types[i].getQualifiedSourceName() + "();"
                                + "}");
                            } else {
                                sourceWriter.println("   else if(\""
                                + types[i].getQualifiedSourceName()
                                + "\".equals(className)) {"
                                + " return new "
                                + types[i].getQualifiedSourceName() + "();"
                                + "}");
                            }
                            count++;
                        }
                    }
                }
                sourceWriter.println("return null;");
                sourceWriter.println("}");
                sourceWriter.println("public void createAsync(final String className, final AsyncClient asyncClient) {");
                for (JClassType reflectableType : reflectableTypes) {
                    JClassType[] types = typeOracle.getTypes();
                    int count = 0;
                    for (int i = 0; i < types.length; i++) {
                        if (types[i].isInterface() == null
                        && !types[i].isAbstract()
                        && types[i].isAssignableTo(reflectableType)) {
                            logger.log(TreeLogger.INFO, "Emitting async instantiation code for: " + types[i].getQualifiedSourceName(), null);
                            if (count == 0) {
                                sourceWriter.println("   if(\""
                                + types[i].getQualifiedSourceName()
                                + "\".equals(className)) {"
                                + "com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() {"
                                + "public void onFailure(Throwable err) {"
                                + "asyncClient.onUnavailable();"
                                + "}"
                                + "public void onSuccess() {"
                                + "asyncClient.onSuccess(new "
                                + types[i].getQualifiedSourceName() + "());"
                                + "}});}");
                            } else {
                                sourceWriter.println("   else if(\""
                                + types[i].getQualifiedSourceName()
                                + "\".equals(className)) {"
                                + "com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() {"
                                + "public void onFailure(Throwable err) {"
                                + "asyncClient.onUnavailable();"
                                + "}"
                                + "public void onSuccess() {"
                                + "asyncClient.onSuccess(new "
                                + types[i].getQualifiedSourceName() + "());"
                                + "}});}");
                            }
                            count++;
                        }
                    }
                }
                sourceWriter.println("}");
                sourceWriter.commit(logger);
                logger.log(TreeLogger.INFO, "Done Generating source for "
                + clazz.getName(), null);
            }
            return clazz.getQualifiedSourceName() + "Wrapper";
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new UnableToCompleteException();
        }
    }

    public SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "Wrapper";
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
        composer.addImplementedInterface("org.broadleafcommerce.openadmin.client.reflection.Factory");
        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) {
            return null;
        } else {
            SourceWriter sw = composer.createSourceWriter(context, printWriter);
            return sw;
        }
    }

}
