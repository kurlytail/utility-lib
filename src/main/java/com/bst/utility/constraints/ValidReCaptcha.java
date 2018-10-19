package com.bst.utility.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.bst.utility.validation.ReCaptchaConstraintValidator;

@Documented
@Constraint(validatedBy = ReCaptchaConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidReCaptcha {

	Class<?>[] groups() default {};

	String message() default "Invalid ReCaptcha";

	Class<? extends Payload>[] payload() default {};
}