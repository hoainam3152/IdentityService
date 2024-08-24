package com.example.buoi_02.validatior;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//@Target annotation này dùng để chú thích phạm vi sử dụng, như FIELD chỉ sử dụng ở các biến
@Target({ FIELD })//@Retention Dùng để chú thích mức độ tồn tại của một annotation nào đó //hay annotation này sẽ được xử lý lúc nào.
@Retention(RUNTIME)		//Xử lý tại thời điểm RUNTIME
@Constraint(validatedBy = { DobValidator.class })	//cái class sẽ chịu trách nhiệm Validate cho annotation này
public @interface DobConstraint {
	//Có 3 property cơ bản của 1 annotation đối với validation
	
	String message() default "Invalid date of birth";

	int min() default 0;
	
	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
