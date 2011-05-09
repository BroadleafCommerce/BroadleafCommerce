package org.broadleafcommerce.gwt.server.service.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public class CategoryCustomPersistenceHandler implements CustomPersistenceHandler {
	
	private static final Log LOG = LogFactory.getLog(CategoryCustomPersistenceHandler.class);

	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && customCriteria.length > 0 && customCriteria[0].equals("OrphanedCategoryListDataSource");
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom add not supported");
	}

	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Map<String, FieldMetadata> categoryProperties = getMergedProperties(Category.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
			Object primaryKey = helper.getPrimaryKey(entity, categoryProperties);
			Category category = (Category) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			
			{
				//Use JPA 2.0 criteria support to load all Categories whose defaultParentCategory equals this one
				CriteriaBuilder criteriaBuilder = dynamicEntityDao.getEntityManager().getCriteriaBuilder();
				CriteriaQuery<Category> query = criteriaBuilder.createQuery(Category.class);
				Root<CategoryImpl> root= query.from(CategoryImpl.class);
				query.where(criteriaBuilder.equal(root.get("defaultParentCategory"), category)); 
				query.select(root); 
				TypedQuery<Category> categoryQuery = dynamicEntityDao.getEntityManager().createQuery(query); 
				List<Category> categories = categoryQuery.getResultList();
				if (!categories.isEmpty()) {
					StringBuffer sb = new StringBuffer();
					sb.append("Cannot delete category (");
					sb.append(category.getId());
					sb.append(",");
					sb.append(category.getName());
					sb.append("). There are Categories that reference it as the default parent category. These categories must first be updated to a different default parent category before this category can be deleted. ");
					sb.append("\nThe categories in question are: ");
					for (Category myCategory : categories) {
						sb.append("\n");
						sb.append("(");
						sb.append(myCategory.getId());
						sb.append(",");
						sb.append(myCategory.getName());
						sb.append(")");
					}
					throw new ServiceException(sb.toString());
				}
			}
			
			{
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
			}
			
			dynamicEntityDao.remove(category);
		} catch (ServiceException e) {
			LOG.error("Unable to remove entity for " + entity.getType()[0], e);
			throw e;
		} catch (Exception e) {
			LOG.error("Unable to remove entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
		
	}

	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom update not supported");
	}

	protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(), 
			entities, 
			null, 
			new String[]{}, 
			new ForeignKey[]{},
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields, 
			excludeManyToOneFields,
			null,
			""
		);
		
		return mergedProperties;
	}
}
