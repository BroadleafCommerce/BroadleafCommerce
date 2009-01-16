import org.springcommerce.extensibility.MergeClassPathXMLApplicationContext;
import com.springcommerce.demo.framework.service.HumanService;

public class DemoUser {

	public static void main(String[] items) {
		MergeClassPathXMLApplicationContext applicationContext = new MergeClassPathXMLApplicationContext(
				new String[]{"applicationContext-framework.xml", "applicationContext-framework-processing.xml"},
				new String[]{"applicationContext-user-processing.xml"}
		);
		HumanService human = (HumanService) applicationContext.getBean("humanService");
		human.updateAge(new Long(1L));
	}
	
}
