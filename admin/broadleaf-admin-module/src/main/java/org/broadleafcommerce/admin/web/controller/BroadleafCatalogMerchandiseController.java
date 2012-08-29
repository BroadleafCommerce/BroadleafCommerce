/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.admin.web.controller;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafCatalogMerchandiseController extends BroadleafAbstractController {

    protected static String categoryView = "/blcadmin/catalog/category";
    protected static String productView = "/blcadmin/catalog/product";
    protected static String productOptionView = "/blcadmin/catalog/productOption";
    protected static String promotionView  = "/blcadmin/catalog/promotion";

    public String category(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getCategoryView();
    }

    public String product(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getProductView();
    }

    public String productOption(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getProductOptionView();
    }

    public String promotion(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getPromotionView();
    }

    public static String getCategoryView() {
        return categoryView;
    }

    public static void setCategoryView(String categoryView) {
        BroadleafCatalogMerchandiseController.categoryView = categoryView;
    }

    public static String getProductView() {
        return productView;
    }

    public static void setProductView(String productView) {
        BroadleafCatalogMerchandiseController.productView = productView;
    }

    public static String getProductOptionView() {
        return productOptionView;
    }

    public static void setProductOptionView(String productOptionView) {
        BroadleafCatalogMerchandiseController.productOptionView = productOptionView;
    }

    public static String getPromotionView() {
        return promotionView;
    }

    public static void setPromotionView(String promotionView) {
        BroadleafCatalogMerchandiseController.promotionView = promotionView;
    }
}

