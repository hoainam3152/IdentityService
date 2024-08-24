package com.example.buoi_02.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.buoi_02.dto.request.ApiResponse;
import com.example.buoi_02.dto.request.UserCreationRequest;
import com.example.buoi_02.dto.request.UserUpdateRequest;
import com.example.buoi_02.dto.response.UserResponse;
import com.example.buoi_02.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;

//	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
//		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
//
//		apiResponse.setResult(userService.createUser(request));
//
//		return apiResponse;
		return ApiResponse.<UserResponse>builder()
				.result(userService.createUser(request))
				.build();
	}

	@GetMapping
	ApiResponse<List<UserResponse>> getUsers() {
		//api này phải đăng nhập mới dùng đc
		//trong spring để get thông tin hiện tại đang đăng nhập
		//đang đc authenticate trong 1 request thì sử dụng SecurityContextHolder
		var authentication = SecurityContextHolder.getContext().getAuthentication(); 
		log.info("Username: {}", authentication.getName());
		authentication.getAuthorities().forEach(
				grantedAuthority -> log.info("Roles: {}", grantedAuthority.getAuthority())
		);
		
		return ApiResponse.<List<UserResponse>>builder()
				.result(userService.getUsers())
				.build();
//		return userService.getUsers();
	}

	@GetMapping("/{userId}")
//	User getUser(@PathVariable("userId") String userId) {
	ApiResponse<UserResponse> getUser(@PathVariable String userId) {
		return ApiResponse.<UserResponse>builder()
				.result(userService.getUser(userId))
				.build();
//		return userService.getUser(userId);
	}
	
	@GetMapping("/myInfo")
//	User getUser(@PathVariable("userId") String userId) {
	ApiResponse<UserResponse> getMyinfo() {
		return ApiResponse.<UserResponse>builder()
				.result(userService.getMyInfo())
				.build();
//		return userService.getUser(userId);
	}

	@PutMapping("/{userId}")
	ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return ApiResponse.<UserResponse>builder()
				.result(userService.updateUser(userId, request))
				.build();
//		return userService.updateUser(userId, request);
	}

	@DeleteMapping("/{userId}")
	ApiResponse<String> deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ApiResponse.<String>builder()
				.result("User has been deleted")
				.build();
//		return "User has been deleted";
	}
}
