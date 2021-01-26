package org.springbridge.action;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springbridge.action.ActionMapping;
import org.springbridge.action.DynaActionForm;
import org.springbridge.web.method.support.ActionMappingConfigMethodProcessor;
import org.springframework.http.CacheControl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CacheControl cc = CacheControl.noCache().mustRevalidate();
		System.out.println(cc.getHeaderValue());

		byte bx = 0;
		short sx = 0;
		int x = sx;
		float f = x;
		long l = x;
		Properties properties = new Properties();
		System.out.println(Properties.class);
		String roles = "Anoop ,AVS,Anoops ,  Anika";
		ActionMapping mapping = new ActionMapping();
		mapping.setRoles(roles);
		for (String role : mapping.getRoleNames()) {
			System.out.println("#" + role + "#");
		}
		mapping.freeze();
		System.out.println(String.format("Unable to locate matching Validator for Form '%s'", "Anika"));
		// mapping.setCancellable(true);
		System.out.println(String.format(ActionMappingConfigMethodProcessor.ILLEGAL_CONFIG_MSG, "Anoop", "Anoop"));
		System.out.println(String.format("Current ModelAttribute class ['%s'] is not an Instance of ['%s'].",
				roles.getClass(), Properties.class));
		;
		class MyDynaActionForm extends DynaActionForm {

			private static final long serialVersionUID = 1L;
			String name = "avs";

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

		}
		;
		DynaActionForm daf = new MyDynaActionForm();
		System.out.println(daf);
		System.out.println(StandardCharsets.UTF_8.name());
		System.out.println("isAssignableFrom>>"+Exception.class.isAssignableFrom(ConfigurationException.class));
	}

}
