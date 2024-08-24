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
import com.example.buoi_02.dto.request.RoleRequest;
import com.example.buoi_02.dto.response.RoleResponse;
import com.example.buoi_02.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
	private final RoleService service;
	
	@PostMapping
	ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
		return ApiResponse.<RoleResponse>builder()
				.result(service.create(request))
				.build();
	}
	
	@GetMapping
	ApiResponse<List<RoleResponse>> getAll() {
		return ApiResponse.<List<RoleResponse>>builder()
				.result(service.getAll())
				.build();
	}
	
	@DeleteMapping("/{role}")
	ApiResponse<Void> delete(@PathVariable String role) {
		service.delete(role);
		return ApiResponse.<Void>builder().build(); 
	}
}
