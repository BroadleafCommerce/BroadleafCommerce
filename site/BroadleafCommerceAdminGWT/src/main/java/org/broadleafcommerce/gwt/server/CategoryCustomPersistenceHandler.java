package org.broadleafcommerce.gwt.server;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public class CategoryCustomPersistenceHandler implements CustomPersistenceHandler {
	
	private static final Class<Category> handleClass = Category.class;

	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname) {
		return false;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname) {
		return false;
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname) {
		try {
			return handleClass.isAssignableFrom(Class.forName(ceilingEntityFullyQualifiedClassname));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname) {
		return false;
	}

	public CustomFetchResponse fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Serializable add(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException {
		throw new RuntimeException("custom add not supported");
	}

	public void remove(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException {
		Category category = (Category) instance;
		//Use JPA 2.0 criteria support to load all Products whose defaultCategory equals this one
		CriteriaBuilder criteriaBuilder = dynamicEntityDao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
		Root<ProductImpl> root= query.from(ProductImpl.class);
		query.where(criteriaBuilder.equal(root.get("defaultCategory"), category)); 
		query.select(root); 
		TypedQuery<Product> productQuery = dynamicEntityDao.getEntityManager().createQuery(query); 
		List<Product> products = productQuery.getResultList();
		if (!products.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Cannot delete category (");
			sb.append(category.getId());
			sb.append(",");
			sb.append(category.getName());
			sb.append("). There are Products that reference it as the default category. These products must first be updated to a different default category before this category can be deleted. ");
			sb.append("\nThe products in question are: ");
			for (Product product : products) {
				sb.append("\n");
				sb.append("(");
				sb.append(product.getId());
				sb.append(",");
				sb.append(product.getName());
				sb.append(")");
			}
			throw new ServiceException(sb.toString());
		}
		
		dynamicEntityDao.remove(instance);
		
	}

	public Serializable update(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException {
		throw new RuntimeException("custom update not supported");
	}

}
