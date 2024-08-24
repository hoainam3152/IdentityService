package com.example.buoi_02.validatior;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate>{

	private int min;
	
	//hàm này sẽ xử lý xem data có đúng hay không
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		
		if (Objects.isNull(value)) {
			return true;
		}
		
		long year = ChronoUnit.YEARS.between(value, LocalDate.now());
		
		return year >= min;
	}

	//hàm này sẽ khởi tạo mỗi constraint này đc khởi tạo và get được các thông số của annotation đó
	@Override
	public void initialize(DobConstraint constraintAnnotation) {
		// TODO Auto-generated method stub
		ConstraintValidator.super.initialize(constraintAnnotation);
		min = constraintAnnotation.min();
	}
}
