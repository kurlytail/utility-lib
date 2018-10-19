package com.bst.utility.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.bst.utility.constraints.PasswordMatches;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(final PasswordMatches constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
		return false;
	}
}
