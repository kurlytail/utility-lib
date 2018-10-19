package com.bst.utility.testlib;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class SeleniumTestExecutionListener extends DependencyInjectionTestExecutionListener {

	private WebDriver driver = null;

	@Override
	public void afterTestClass(final TestContext testContext) throws Exception {
		if (this.driver != null) {
			this.driver.close();
		}
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		final ApplicationContext context = testContext.getApplicationContext();
		if ((context instanceof ConfigurableApplicationContext) && (this.driver == null)) {
			final SeleniumTest annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(),
					SeleniumTest.class);
			final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context)
					.getBeanFactory();
			this.driver = beanFactory.createBean(annotation.driver());
			beanFactory.registerSingleton("webDriver", this.driver);
		}
	}
}
