[![Code Quality Score](https://api.codiga.io/project/18432/score/svg?)](https://frontend.code-inspector.com/public/project/18432/spring-bridge-v1/dashboard)
[![Code Grade](https://api.codiga.io/project/18432/status/svg?)](https://frontend.code-inspector.com/public/project/18432/spring-bridge-v1/dashboard)

#Spring Bridge Framework

##### Spring Bridge Framework - A simple adaptor framework designed to reuse existing  Java code written on top of Apache Struts 1.x Framework.This doesn't include Struts 1.x taglibs or its extentions (html,logic,bean,nested etc).All UI components needs to be migrated to JSTL or Spring Taglibs .The existing code can be reused by introducing bridge method which contains Spring Bridge Framework code  and couple of Spring Framework specific Annotations

#####			One of the Major goal of any App modernization project is reduce the security vulnerabilities in obsolete legacy framework by moving to the proven new framework (Most of the people discuss only about moving to cloud platform). This  requires considerable amount of development and testing effort ,In case of applications developed over 15 to 20 years time there will be lack of application SMEs knowledge also. So we need some adaptor framework and automated migration with minimal developer intervention. Spring Bridge Framework is designed for such a use case in mind. Struts 1.x and Spring MVC are architecturally similar ,So the easiest target path for Struts 1.x is Spring MVC. Spring Bridge Framework just provides the missing Legos in this journey. As a developer I think there is no need to migrate each and every applications to Microservices and Modern JS UI ,So still we need to maintain the classic web applications.This framework is inspired by people overhauling/restoring the classics in other areas. Here we take a classic application and port it to Modern stack. Once we port it to modern stack we can deploy it in cloud and business and technology teams will start reap benefit from it.

##### Apache Struts 1.x and Spring MVC are two great frameworks which really changed the way people write web applications, written by two great developers Craig R. McClanahan and Rod Johnson and Valuable contributions from lots of developers. For organizations invested heavily on Struts 1.x (Common practice was use Struts for web tier and Spring for all other Layers) has no direct support to move seamlessly to Spring MVC, So I wrote a small adaptor framework to reuse the existing Struts 1.x code defined migration strategies using the framework and used it to migrate more than 100 applications. Over the course of action ,I have learned various strategies to reuse the existing code and wrote Spring extensions to keep behavioral consistency.Once the migration project is over I want to put it all together and create the next version of original framework,which reduces the code generation complexities and incorporate my learnings in migration ,Spring Bridge is my attempt. This version is not battle tested, but provide a good starting point. In this framework I only mapped limited set of Struts 1.x features to Spring MVC. Currently there is no support for Struts Module concept.
 
## Prerequisites

- Maven 3.8.1 or higher
- Java 11 or higher
- Spring framework 5.3 or higher

##### To reuse the existing Struts 1.x code , Change the Struts imports to Struts-bridge one.
Introduce the bridge method with corresponding Spring Annotations.Autowire ActionHandler interface and then 
use it to call existing struts 1.x execute/perform method - **handler.handleActionExecution(ctx)**

_**This version is not battle tested_

```java
		package org.sb.examples;
		
		import javax.servlet.http.HttpServletRequest;
		import javax.servlet.http.HttpServletResponse;
		
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.stereotype.Controller;
		import org.springframework.validation.BindingResult;
		import org.springframework.web.bind.annotation.ModelAttribute;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.servlet.ModelAndView;
		import org.springbridge.action.Action;
		import org.springbridge.action.ActionForm;
		import org.springbridge.action.ActionForward;
		import org.springbridge.action.ActionMapping;
		import org.springbridge.support.handler.ActionContext;
		import org.springbridge.support.handler.ActionHandler;
		import org.springbridge.utils.ModelAttributeUtils;
		@Controller
		public class ProcessSimpleAction extends Action {
			@Autowired
			private ActionHandler handler;

		    public ProcessSimpleAction() {
		        super();
		    }
		    
			@ModelAttribute("simpleForm")
			public ActionForm loginForm() {
				return ModelAttributeUtils.lookupOrCreateModelAttribute("org.sb.examples.simple.SimpleActionForm");
			}
			
		    @RequestMapping(path = "/processSimple.do")
			public ModelAndView handlePrepareSimple(ActionMapping mapping,
					@ModelAttribute("simpleForm") ActionForm form, BindingResult bindingResult, HttpServletRequest request,
					HttpServletResponse response) {
				final ActionContext ctx = ActionContext.builder().withController(this).withExecuteFunction(this::execute)
						.withMapping(mapping).withMapping(mapping).withHttpServletRequest(request)
						.withHttpServletResponse(response).build();
				ModelAndView mav= handler.handleActionExecution(ctx);
				return mav;
			}
```
##### Or You can create the model attribute in normal way as shown below.

```java

@Controller
public class UserAction extends Action {

	private final static String SUCCESS = "success";
	@Autowired
	private ActionHandler handler;

	public UserAction() {
		super();
	}

	@ModelAttribute
	public ActionForm userForm() {
		UserForm user = new UserForm("123");
		return user;
	}

	@RequestMapping(path = "/UserAction.do")
	public ModelAndView handlePrepareDyna(ActionMapping mapping, ActionForm form, BindingResult bindingResult,
			HttpServletRequest request, HttpServletResponse response) {
		UserForm userForm = (UserForm) form;
		System.out.println("UserForm::" + userForm);
		final ActionContext ctx = ActionContext.builder().withController(this).withMapping(mapping).withForm(form)
				.withBindingResult(bindingResult).withHttpServletRequest(request)
				.withHttpServletResponse(response).build(this::setExecuteFunction);
		return handler.handleActionExecution(ctx);
	}
	// ---------------------------------------------------------- Action Methods

	private void setExecuteFunction(Builder builder) {
		ActionMapping mapping = builder.getMapping();
		HttpServletRequest request = builder.getHttpServletRequest();
		String methodName = request.getParameter(mapping.getParameter());
		if (StringUtils.hasText(methodName)) {
			switch (methodName) {
			case "add":
				builder.setExecuteFunction(this::add);
				break;
			case "update":
				builder.setExecuteFunction(this::update);
				break;
			case "delete":
				builder.setExecuteFunction(this::delete);
				break;
			default:
				throw new UnsupportedOperationException(methodName + " not found !!!");
			}
		}
	}

	public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserForm userForm = (UserForm) form;
		userForm.setMessage("Inside add user method.");
		return mapping.findForward(SUCCESS);
	}
```

<table>
  <tr><th>Struts 1.x </th><th>Spring Bridge Framework</th></tr>
  <tr><td>Concrete class like Action,ActionForms </td><td>Concrete class remain as such ,but imports needs to be changed from Struts to Spring Bridge.Use Spring MVC specific Annotations like Controller,SessionAttributes,ModelAttribute etc</td></tr>
  <tr><td>XML Config based DynaActionForm  </td><td>DynaActionForms need to convert to POJOS and Parent must be DynaActionForm from Spring Bridge.Ensure bean properties follows proper JavaBean naming convention.</td></tr>
  <tr><td>RequestProcessor </td><td>Replaced with ActionHandler.Evaluate default(DefaultActionHandler) is sufficient for your Application</td></tr>
  <tr><td>MessageResources in Struts </td><td>Use MessageResources from Spring Bridge  </td></tr>
</table>

## Examples

[Spring Bridge Sample App](https://github.com/anoopvs/sb-cookbook)

As a Java developer, This is my first attempt to give something back to community. 

Spring Bridge Framework only contains migration strategies for limited set of Struts features. You must carefully evaluate this before choosing. This framework contains code reuse strategies I have refined over 18-month time while migrating Struts projects(In some areas I still may me narrow minded while addressing the problems). I only dealt with one Struts project which uses Module concept and View was Struts Tiles. Tiles 3 has retired, So You must evaluate some other View Technology. I haven't defined any specific strategies for Struts Module concept, but put some extension points, You can evaluate it suits your need. Most of the projects view part was JSP and Struts tags and Velocity ,Migrated them to JSTL, Spring-Tags and Velocity 3.
For any queries or feedback regarding Spring Bridge Framework you can mail me [Anoop V S](mailto:email128@gmail.com).This is my developer mail id.I will try to respond your queries during my weekends.

## Changelog
#### Fixes
##### 2021-04-11 - Fixed minor code violations pointed by code-inspector. 
