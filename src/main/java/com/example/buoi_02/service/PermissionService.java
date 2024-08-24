package com.example.buoi_02.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.buoi_02.dto.request.PermissionRequest;
import com.example.buoi_02.dto.response.PermissionResponse;
import com.example.buoi_02.entity.Permission;
import com.example.buoi_02.mapper.PermissionMapper;
import com.example.buoi_02.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

	private final PermissionRepository repository;
	private final PermissionMapper mapper;
	
	public PermissionResponse create(PermissionRequest request) {
		Permission permission = mapper.toPermission(request);
		permission = repository.save(permission);
		return mapper.toPermissionResponse(permission);
	}
	
	public List<PermissionResponse> getAll() {
		var permissions = repository.findAll();
		return permissions.stream().map(mapper::toPermissionResponse).toList();
	}
	
	public void delete(String permission) {
		repository.deleteById(permission);
	}
}
