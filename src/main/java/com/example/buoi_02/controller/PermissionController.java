package com.example.buoi_02.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.buoi_02.dto.request.ApiResponse;
import com.example.buoi_02.dto.request.PermissionRequest;
import com.example.buoi_02.dto.response.PermissionResponse;
import com.example.buoi_02.service.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
	private final PermissionService service;
	
	@PostMapping
	ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
		return ApiResponse.<PermissionResponse>builder()
				.result(service.create(request))
				.build();
	}
	
	@GetMapping
	ApiResponse<List<PermissionResponse>> getAll() {
		return ApiResponse.<List<PermissionResponse>>builder()
				.result(service.getAll())
				.build();
	}
	
	@DeleteMapping("/{permission}")
	ApiResponse<Void> delete(@PathVariable String permission) {
		service.delete(permission);
		return ApiResponse.<Void>builder().build(); 
	}
}
