package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will on order confirmation page, submit order
 * information via javascript to google analytics.
 * 	<blc:googleAnalytics th:attr="orderNumber=${order != null ? order.orderNumber : null}" />
 *	<script th:utext="${analytics}"></script>
 * @author tleffert
 */
public class GoogleAnalyticsProcessor extends
		AbstractModelVariableModifierProcessor {

	@Resource(name = "blOrderService")
	protected OrderService orderService;

	@Value("${googleAnalytics.webPropertyId}")
	private String webPropertyId;

	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
	public GoogleAnalyticsProcessor() {
		super("googleanalytics");
	}

	@Override
	public int getPrecedence() {
		return 100000;
	}

	@Override
	protected void modifyModelAttributes(Arguments arguments, Element element) {

		String orderNumber = element.getAttributeValue("orderNumber");
		Order order = null;
		if (orderNumber != null) {
			order = orderService.findOrderByOrderNumber(orderNumber);
		}
		addToModel(arguments, "analytics", analytics(webPropertyId, order));

	}

	/**
	 * Documentation for the recommended asynchronous GA tag is at:
	 * http://code.google
	 * .com/apis/analytics/docs/tracking/gaTrackingEcommerce.html
	 * 
	 * @param webPropertyId
	 *            - Google Analytics ID
	 * @param order
	 *            - optionally track the order submission. This should be
	 *            included on the page after the order has been sucessfully
	 *            submitted. If null, this will just track the current page
	 * @return the relevant Javascript to render on the page
	 */
	protected String analytics(String webPropertyId, Order order) {
		StringBuffer sb = new StringBuffer();

		sb.append("var _gaq = _gaq || [];");
		sb.append("_gaq.push(['_setAccount', '" + webPropertyId + "']);");
		sb.append("_gaq.push(['_trackPageview']);");
		// sb.append("_gaq.push(['_setDomainName', '127.0.0.1']);"); -- for testing locally
		if (order != null) {
			Address paymentAddress = getBillingAddress(order);
			if (paymentAddress != null) {
				sb.append("_gaq.push(['_addTrans','" + order.getId() + "'");
				sb.append(",'" + order.getName() + "'");
				sb.append(",'" + order.getTotal() + "'");
				sb.append(",'" + order.getTotalTax() + "'");
				sb.append(",'" + order.getTotalShipping() + "'");
				sb.append(",'" + paymentAddress.getCity() + "'");
				sb.append(",'" + paymentAddress.getState().getName() + "'");
				sb.append(",'" + paymentAddress.getCountry().getName() + "'");
				sb.append("]);");
			} 
			for (FulfillmentGroup fulfillmentGroup : order
					.getFulfillmentGroups()) {
				for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup
						.getFulfillmentGroupItems()) {
					DiscreteOrderItem orderItem = (DiscreteOrderItem) fulfillmentGroupItem
							.getOrderItem();
					sb.append("_gaq.push(['_addItem','" + order.getId() + "'");
					sb.append(",'" + orderItem.getSku().getId() + "'");
					sb.append(",'" + orderItem.getSku().getName() + "'");
					sb.append(",' "
							+ orderItem.getProduct().getDefaultCategory() + "'");
					sb.append(",'" + orderItem.getPrice() + "'");
					sb.append(",'" + orderItem.getQuantity() + "'");
					sb.append("]);");
				}
			}
			sb.append("_gaq.push(['_trackTrans']);");
		}

		sb.append(" (function() {"
				+ "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"
				+ "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"
				+ "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"
				+ "})();");

		return sb.toString();
	}

	protected Address getBillingAddress(Order order) {
		PaymentInfo paymentInfo = null;
		if (order.getPaymentInfos().size() > 0) {
			paymentInfo = order.getPaymentInfos().get(0);
		}

		Address address = null;
		if (paymentInfo == null || paymentInfo.getAddress() == null) {
			// in this case, no payment info object on the order or no billing
			// information recieved due to external payment gateway
			address = order.getFulfillmentGroups().get(0).getAddress();
		} else {
			// then the address must exist on the payment info
			address = paymentInfo.getAddress();
		}

		return address;
	}
}
