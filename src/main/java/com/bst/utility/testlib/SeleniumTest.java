package com.bst.utility.testlib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SeleniumTest {

	String baseUrl() default "http://localhost:8080";

	Class<? extends WebDriver> driver() default SafariDriver.class;
}