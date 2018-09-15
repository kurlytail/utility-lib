package com.bst.utility.constraints;

import javax.validation.Payload;

import com.bst.utility.validation.ReCaptchaConstraintValidator;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ReCaptchaConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidReCaptcha {

	String message() default "Invalid ReCaptcha";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}