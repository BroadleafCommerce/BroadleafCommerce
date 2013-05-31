package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productSummary")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductSummaryWrapper extends BaseWrapper implements APIWrapper<Product> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String name;

    @XmlElement
    protected String description;

    @XmlElement(name = "productOption")
    @XmlElementWrapper(name = "productOptions")
    protected List<ProductOptionWrapper> productOptions;

    @Override
    public void wrap(Product model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();

        if (model.getProductOptions() != null && !model.getProductOptions().isEmpty()) {
            this.productOptions = new ArrayList<ProductOptionWrapper>();
            List<ProductOption> options = model.getProductOptions();
            for (ProductOption option : options) {
                ProductOptionWrapper optionWrapper = (ProductOptionWrapper) context.getBean(ProductOptionWrapper.class.getName());
                optionWrapper.wrap(option, request);
                this.productOptions.add(optionWrapper);
            }
        }
    }
}
