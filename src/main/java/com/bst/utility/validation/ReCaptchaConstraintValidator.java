package com.bst.utility.validation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bst.utility.constraints.ValidReCaptcha;
import com.bst.utility.services.ReCaptchaService;

public class ReCaptchaConstraintValidator implements ConstraintValidator<ValidReCaptcha, String> {

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Override
    public void initialize(ValidReCaptcha constraintAnnotation) {

    }
    
    @Value("${bst.user.captchaDisable:false}")
    public Boolean captchaDisabled;

    @Override
    public boolean isValid(String reCaptchaResponse, ConstraintValidatorContext context) {

    	if (captchaDisabled) {
    		return true;
    	}
    	
        if (reCaptchaResponse == null || reCaptchaResponse.isEmpty()){
            return true;
        }

        return reCaptchaService.validate(reCaptchaResponse);
    }

}