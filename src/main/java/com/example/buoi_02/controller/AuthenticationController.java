package com.example.buoi_02.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.buoi_02.dto.request.ApiResponse;
import com.example.buoi_02.dto.request.AuthenticationRequest;
import com.example.buoi_02.dto.request.IntrospectRequest;
import com.example.buoi_02.dto.request.LogoutRequest;
import com.example.buoi_02.dto.request.RefreshRequest;
import com.example.buoi_02.dto.response.AuthenticationResponse;
import com.example.buoi_02.dto.response.IntrospectResponse;
import com.example.buoi_02.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/token")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) { 
		var result = authenticationService.authenticate(request);
		return ApiResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}
	
	//Logout này chỉ tạm thời lưu các id token đã hết hạn vào Database
		//Nên vì vậy cần tìm hiểu thêm cronjob để xoá các id đó sau 1 khoảng thời gian
		//để database nó nhẹ đi
	@PostMapping("/logout")
	ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
		authenticationService.logout(request);
		return ApiResponse.<Void>builder().build();
	}
	
	@PostMapping("/refresh")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request) 
			throws JOSEException, ParseException { 
		var result = authenticationService.refreshToken(request);
		return ApiResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}

	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
			throws JOSEException, ParseException {

		var result = authenticationService.introspect(request);
		return ApiResponse.<IntrospectResponse>builder()
				.result(result)
				.build();
	}
}
