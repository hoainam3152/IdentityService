 package com.example.buoi_02.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.buoi_02.dto.request.ApiResponse;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private static final String MIN_ATTRIBUTE = "min";
	
	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse<?>> handlingException(RuntimeException exception) {
		ApiResponse<?> apiResponse = new ApiResponse<>();

		apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
		apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

		return ResponseEntity.badRequest().body(apiResponse);
	}

//	@ExceptionHandler(value = RuntimeException.class)
//	ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
//		ApiResponse apiResponse = new ApiResponse();
//		apiResponse.setCode(1001);
//		apiResponse.setMessage(exception.getMessage());
//		return ResponseEntity.badRequest().body(apiResponse);
//	}

	@ExceptionHandler(value = AppException.class)
	ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {

		ErrorCode errorCode = exception.getErrorCode();

		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());

		return ResponseEntity
				.status(errorCode.getStatusCode())
				.body(apiResponse);
	}

	@SuppressWarnings("unchecked")
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse<?>> handlingVadidation(MethodArgumentNotValidException exception) {

		String enumKey = exception.getFieldError().getDefaultMessage();
		ErrorCode errorCode = ErrorCode.INVALID_KEY;

		Map<String, Object> attributes = null;
		try {
			errorCode = ErrorCode.valueOf(enumKey);
			
			//
			var constraintViolation = exception.getBindingResult()	//BindingResult này chính là những error mà cái MethodArgumentNotValidException nó wrap lại
					.getAllErrors()	//lấy toàn bộ error
					.getFirst()		//lấy cái đầu tiên
					.unwrap(ConstraintViolation.class);	//sau khi unwrap ta sẽ có đc 1 object chứ những thông tin mong muốn (ở đây là attribute)
			
			attributes = constraintViolation.getConstraintDescriptor()	//lấy nội dung của annotation
							   					.getAttributes();		//lấy được thông tin chi tiết của từng param truyền vào
			
			log.info(attributes.toString());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(Objects.nonNull(attributes) ? 
				mapAttribute(errorCode.getMessage(), attributes) 
				: errorCode.getMessage());
//		apiResponse.setMessage(errorCode.getMessage());

		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	private String mapAttribute(String message, Map<String, Object> attributes) {
		String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
		
		return message.replace("{" + MIN_ATTRIBUTE +"}", minValue);
	}
	
	//define 1 exception cần handling
	@ExceptionHandler(value = AccessDeniedException.class)
	ResponseEntity<ApiResponse<?>> handlingAccess(AccessDeniedException exception) {
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
		
		return ResponseEntity.status(errorCode.getStatusCode()).body(
					ApiResponse.builder()
					.code(errorCode.getCode())
					.message(errorCode.getMessage())
					.build()
				);
	}
}
