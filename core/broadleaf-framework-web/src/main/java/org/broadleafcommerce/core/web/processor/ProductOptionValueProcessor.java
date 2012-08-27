package org.broadleafcommerce.core.web.processor;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.codehaus.jackson.map.ObjectMapper;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

public class ProductOptionValueProcessor extends AbstractAttrProcessor  {

	private static final Log LOG = LogFactory.getLog(ProductOptionValueProcessor.class);
	
	public ProductOptionValueProcessor() {
		super("product_option_value");
	}
	
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		ProductOptionValue productOptionValue = (ProductOptionValue) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
		ProductOptionValueDTO dto = new ProductOptionValueDTO();
		dto.setOptionId(productOptionValue.getProductOption().getId());
		dto.setValueId(productOptionValue.getId());
		dto.setValueName(productOptionValue.getAttributeValue());
		if (productOptionValue.getPriceAdjustment() != null) {
			dto.setPriceAdjustment(productOptionValue.getPriceAdjustment().getAmount());
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			Writer strWriter = new StringWriter();
			mapper.writeValue(strWriter, dto);
			element.setAttribute("data-product-option-value", strWriter.toString());
			return ProcessorResult.OK;
		} catch (Exception ex) {
			LOG.error("There was a problem writing the product option value to JSON", ex);
		}
		
		return null;
		
	}

	@Override
	public int getPrecedence() {
		return 10000;
	}

	private class ProductOptionValueDTO {
		private Long optionId;
		private Long valueId;
		private String valueName;
		private BigDecimal priceAdjustment;
		@SuppressWarnings("unused")
		public Long getOptionId() {
			return optionId;
		}
		public void setOptionId(Long optionId) {
			this.optionId = optionId;
		}
		@SuppressWarnings("unused")
		public Long getValueId() {
			return valueId;
		}
		public void setValueId(Long valueId) {
			this.valueId = valueId;
		}
		@SuppressWarnings("unused")
		public String getValueName() {
			return valueName;
		}
		public void setValueName(String valueName) {
			this.valueName = valueName;
		}
		@SuppressWarnings("unused")
		public BigDecimal getPriceAdjustment() {
			return priceAdjustment;
		}
		public void setPriceAdjustment(BigDecimal priceAdjustment) {
			this.priceAdjustment = priceAdjustment;
		}
	}

}
