package com.bst.utility.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bst.utility.constraints.ValidReCaptcha;
import com.bst.utility.services.ReCaptchaService;

public class ReCaptchaConstraintValidator implements ConstraintValidator<ValidReCaptcha, String> {

	@Value("${bst.user.captchaDisable:false}")
	public Boolean captchaDisabled;

	@Autowired
	private ReCaptchaService reCaptchaService;

	@Override
	public void initialize(final ValidReCaptcha constraintAnnotation) {

	}

	@Override
	public boolean isValid(final String reCaptchaResponse, final ConstraintValidatorContext context) {

		if (this.captchaDisabled) {
			return true;
		}

		if ((reCaptchaResponse == null) || reCaptchaResponse.isEmpty()) {
			return false;
		}

		return this.reCaptchaService.validate(reCaptchaResponse);
	}

}