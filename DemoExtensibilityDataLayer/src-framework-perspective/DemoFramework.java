

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.springcommerce.demo.framework.service.CatalogService;
import com.springcommerce.demo.framework.service.HumanService;

public class DemoFramework {

	public static void main(String[] items) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"applicationContext-framework.xml", "applicationContext-framework-processing.xml"});
		HumanService human = (HumanService) applicationContext.getBean("humanService");
		human.createNewPerson();
		human.updateAge(new Long(1L));
		CatalogService catalog = (CatalogService) applicationContext.getBean("catalogService");
		catalog.createNewCatalog();
		catalog.updateCatalog(new Long(1L));
	}
	
}
