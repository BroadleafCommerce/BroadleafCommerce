#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.client;

import com.google.gwt.core.client.GWT;
import org.broadleafcommerce.${artifactId}.client.MerchandisingModule;
import org.broadleafcommerce.open${artifactId}.client.reflection.ModuleFactory;

public class MyCompanyAdminModule extends MerchandisingModule {
	
	public static final MyCompanyAdminMessages ADMINMESSAGES = GWT.create(MyCompanyAdminMessages.class);
	
	public void onModuleLoad() {
        super.onModuleLoad();

		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put("product", "${package}.${artifactId}.client.view.product.MyCompanyProductView");
		moduleFactory.put("productPresenter", "${package}.${artifactId}.client.presenter.product.MyCompanyProductPresenter");
	}

    @Override
    public void postDraw() {
        super.postDraw();
        //BLCMain.MASTERVIEW.getModuleTabs().removeTab(0);
    }
}