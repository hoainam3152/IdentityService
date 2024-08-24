package com.example.buoi_02.configuration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.example.buoi_02.dto.request.ApiResponse;
import com.example.buoi_02.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
		
		//Mong muốn trả về Http status code 401
		response.setStatus(errorCode.getStatusCode().value());
		//Muốn trả về body với content type là JSON
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		//Respone body bằng cách tạo 1 api respone
		ApiResponse<?> apiResponse = ApiResponse.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.build();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		//write() cần 1 dữ liệu kiểu String nên cần ObjectMapper đổi Object thành JSON
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
	}

}
