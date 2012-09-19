package org.broadleafcommerce.core.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This processor will add the following information to the model, available for consumption by a template:
 * -pricing for a sku based on the product option values selected
 * -the complete set of product options and values for a given product
 *  
 * @author jfridye
 *
 */
public class ProductOptionsProcessor extends AbstractModelVariableModifierProcessor {

	private static final Log LOG = LogFactory.getLog(ProductOptionsProcessor.class);
	
	public ProductOptionsProcessor() {
		super("product_options");
	}

	@Override
	public int getPrecedence() {
		return 10000;
	}

	@Override
	protected void modifyModelAttributes(Arguments arguments, Element element) {
		CatalogService catalogService = ProcessorUtils.getCatalogService(arguments);
		Long productId = (Long) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue("productId"));
		Product product = catalogService.findProductById(productId);
		if (product != null) {
			addAllProductOptionsToModel(product);
			addProductOptionPricingToModel(product);
		}
	}
	
	private void addProductOptionPricingToModel(Product product) {
		List<Sku> skus = product.getSkus();
		List<ProductOptionPricingDTO> skuPricing = new ArrayList<ProductOptionPricingDTO>();
		for (Sku sku : skus) {
			
			List<Long> productOptionValueIds = new ArrayList<Long>();
			
			List<ProductOptionValue> productOptionValues = sku.getProductOptionValues();
			for (ProductOptionValue productOptionValue : productOptionValues) {
				productOptionValueIds.add(productOptionValue.getId());
			}
			
			Long[] values = new Long[productOptionValueIds.size()];
			productOptionValueIds.toArray(values);
			
			ProductOptionPricingDTO dto = new ProductOptionPricingDTO();
            Money currentPrice;
			if (sku.isOnSale()) {
                currentPrice = sku.getSalePrice();
			} else {
                currentPrice = sku.getRetailPrice();
			}
            dto.setPrice(formatPrice(currentPrice));
			dto.setSelectedOptions(values);
			skuPricing.add(dto);
		}
		writeJSONToModel("skuPricing", skuPricing);
	}
	
	private void addAllProductOptionsToModel(Product product) {
		List<ProductOption> productOptions = product.getProductOptions();
		List<ProductOptionDTO> dtos = new ArrayList<ProductOptionDTO>();
		for (ProductOption option : productOptions) {
			ProductOptionDTO dto = new ProductOptionDTO();
			dto.setId(option.getId());
			dto.setType(option.getType().getType());
			Map<Long, String> values = new HashMap<Long, String>();
			for (ProductOptionValue value : option.getAllowedValues()) {
				values.put(value.getId(), value.getAttributeValue());
			}
			dto.setValues(values);
			dtos.add(dto);
		}
		writeJSONToModel("allProductOptions", dtos);
	}
	
	private void writeJSONToModel(String modelKey, Object o) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Writer strWriter = new StringWriter();
			mapper.writeValue(strWriter, o);
			addToModel(modelKey, strWriter.toString());
		} catch (Exception ex) {
			LOG.error("There was a problem writing the product option map to JSON", ex);
		}
	}

    private String formatPrice(Money price){
        if (price == null){
            return null;
        }
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getJavaLocale() != null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(brc.getJavaLocale());
            format.setCurrency(price.getCurrency());
            return format.format(price.getAmount());
        } else {
            // Setup your BLC_CURRENCY and BLC_LOCALE to display a diff default.
            return "$ " + price.getAmount().toString();
        }
    }
	
	private class ProductOptionDTO {
		private Long id;
		private String type;
		private Map<Long, String> values;
		private String selectedValue;
		@SuppressWarnings("unused")
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		@SuppressWarnings("unused")
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@SuppressWarnings("unused")
		public Map<Long, String> getValues() {
			return values;
		}
		public void setValues(Map<Long, String> values) {
			this.values = values;
		}
		@SuppressWarnings("unused")
		public String getSelectedValue() {
			return selectedValue;
		}
		@SuppressWarnings("unused")
		public void setSelectedValue(String selectedValue) {
			this.selectedValue = selectedValue;
		}
	}
	
	private class ProductOptionPricingDTO {
		private Long[] skuOptions;
		private String price;
		@SuppressWarnings("unused")
		public Long[] getSelectedOptions() {
			return skuOptions;
		}
		public void setSelectedOptions(Long[] skuOptions) {
			this.skuOptions = skuOptions;
		}
		@SuppressWarnings("unused")
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
	}

}
