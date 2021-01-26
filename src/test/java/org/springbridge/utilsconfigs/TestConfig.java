package org.springbridge.utilsconfigs;

import org.springbridge.action.ActionForward;
import org.springbridge.action.ActionMapping;
import org.springbridge.support.ActionMappingInstanceLocator;
import org.springbridge.support.RequestAttributeActionMappingInstanceLocator;
import org.springbridge.support.utils.MessageResources;
import org.springbridge.utils.ApplicationContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class TestConfig {
	@Bean
	public static ApplicationContextUtils applicationContextUtils() {
		return new ApplicationContextUtils();
	}

	// The bean name must always be messageSource
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		final ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
		rrbms.setBasename("classpath:org/sb/resources/MessageResources");
		rrbms.setDefaultEncoding("UTF-8");
		rrbms.setFallbackToSystemLocale(false);
		return rrbms;
	}

	@Bean
	public MessageResources messageResources(final ApplicationContext ctx) {
		return new MessageResources(ctx);
	}

	@Bean("homeMapping")
	public ActionMapping homeMapping() {
		ActionMapping homeMapping = new ActionMapping();
		homeMapping.setPath("/Home.do");
		homeMapping.setInput("/WEB-INF/login/login.jsp");
		homeMapping.setType("org.sb.examples.SuccessAction");
		homeMapping.setValidate(false);
		homeMapping.setRoles("ADMIN ,  CSR");
		// homeMapping.setRoles("UNKNOWN");
		homeMapping.addForward("success", new ActionForward("success", "/index.jsp", true));
		return homeMapping;
	}

	@Bean
	public ActionMappingInstanceLocator actionMappingResolver() {
		return new RequestAttributeActionMappingInstanceLocator();
	}

}
