/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.web.form.TranslationForm;
import org.broadleafcommerce.openadmin.web.form.component.DefaultListGridActions;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridAction;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.ComboField;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("blTranslationFormBuilderService")
public class TranslationFormBuilderServiceImpl implements TranslationFormBuilderService {

    @Resource(name = "blFormBuilderService")
    protected FormBuilderService formBuilderService;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @Override
    public ListGrid buildListGrid(List<Translation> translations, boolean isRte) {
        // Set up the two header fields we're interested in for the translations list grid
        List<Field> headerFields = new ArrayList<Field>();
        headerFields.add(new Field()
                .withName("localeCode")
                .withFriendlyName("Translation_localeCode")
                .withOrder(0));

        headerFields.add(new Field()
                .withName("translatedValue")
                .withFriendlyName("Translation_translatedValue")
                .withOrder(10));

        // Create the list grid and set its basic properties
        ListGrid listGrid = new ListGrid();
        listGrid.getHeaderFields().addAll(headerFields);
        listGrid.setListGridType(ListGrid.Type.TRANSLATION);
        listGrid.setSelectType(ListGrid.SelectType.SINGLE_SELECT);
        listGrid.setCanFilterAndSort(false);

        // Allow add/update/remove actions, but provisioned especially for translation. Because of this, we will clone
        // the default actions so that we may change the class
        ListGridAction addAction = DefaultListGridActions.ADD.clone();
        ListGridAction removeAction = DefaultListGridActions.REMOVE.clone();
        ListGridAction updateAction = DefaultListGridActions.UPDATE.clone();
        addAction.setButtonClass("translation-grid-add");
        removeAction.setButtonClass("translation-grid-remove");
        updateAction.setButtonClass("translation-grid-update");
        listGrid.addToolbarAction(addAction);
        listGrid.addRowAction(updateAction);
        listGrid.addRowAction(removeAction);

        //TODO rework code elsewhere so these don't have to be added
        listGrid.setSectionKey(Translation.class.getCanonicalName());
        listGrid.setSubCollectionFieldName("translation");

        // Create records for each of the entries in the translations list
        for (Translation t : translations) {
            ListGridRecord record = new ListGridRecord();
            record.setListGrid(listGrid);
            record.setId(String.valueOf(t.getId()));

            Locale locale = localeService.findLocaleByCode(t.getLocaleCode());

            record.getFields().add(new Field()
                    .withName("localeCode")
                    .withFriendlyName("Translation_localeCode")
                    .withOrder(0)
                    .withValue(locale.getLocaleCode())
                    .withDisplayValue(locale.getFriendlyName()));

            record.getFields().add(new Field()
                    .withName("translatedValue")
                    .withFriendlyName("Translation_translatedValue")
                    .withOrder(10)
                    .withValue(t.getTranslatedValue())
                    .withDisplayValue(isRte ? getLocalizedEditToViewMessage() : t.getTranslatedValue()));

            listGrid.getRecords().add(record);
        }

        listGrid.setTotalRecords(listGrid.getRecords().size());

        return listGrid;
    }

    @Override
    public EntityForm buildTranslationForm(ClassMetadata cmd, TranslationForm formProperties, TranslationFormAction action) {
        EntityForm ef = new EntityForm();

        EntityFormAction saveAction = DefaultEntityFormActions.SAVE.clone();
        saveAction.setButtonClass("translation-submit-button");
        ef.addAction(saveAction);

        ComboField comboField = getLocaleField(formProperties.getLocaleCode());
        ef.addField(cmd, comboField);

        Field translatedValueValueField = new Field()
                .withName("translatedValue")
                .withFieldType(formProperties.getIsRte() ? "html" : "string")
                .withFriendlyName("Translation_translatedValue")
                .withValue(formProperties.getTranslatedValue())
                .withOrder(10);

        ef.addField(cmd, translatedValueValueField);

        if (action.equals(TranslationFormAction.UPDATE)) {
            comboField.setReadOnly(true);
        }

        ef.addHiddenField(cmd, new Field()
                .withName("ceilingEntity")
                .withValue(formProperties.getCeilingEntity()));

        ef.addHiddenField(cmd, new Field()
                .withName("entityId")
                .withValue(formProperties.getEntityId()));

        ef.addHiddenField(cmd, new Field()
                .withName("propertyName")
                .withValue(formProperties.getPropertyName()));

        ef.addHiddenField(cmd, new Field()
                .withName("isRte")
                .withValue(String.valueOf(formProperties.getIsRte())));

        return ef;
    }

    protected ComboField getLocaleField(String value) {
        ComboField f = new ComboField();
        f.setName("localeCode");
        f.setFriendlyName("Translation_localeCode");
        f.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION.toString());
        f.setOrder(0);

        if (StringUtils.isNotBlank(value)) {
            f.setValue(value);
        }

        List<Locale> locales = localeService.findAllLocales();

        Map<String, String> localeMap = new HashMap<String, String>();
        for (Locale l : locales) {
            localeMap.put(l.getLocaleCode(), l.getFriendlyName());
        }
        f.setOptions(localeMap);

        return f;
    }

    protected String getLocalizedEditToViewMessage() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        if (ctx != null && ctx.getMessageSource() != null) {
            return ctx.getMessageSource().getMessage("i18n.editToView", null, ctx.getJavaLocale());
        }
        return null;
    }

}
