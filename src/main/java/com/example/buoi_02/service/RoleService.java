package com.example.buoi_02.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.buoi_02.dto.request.RoleRequest;
import com.example.buoi_02.dto.response.RoleResponse;
import com.example.buoi_02.mapper.RoleMapper;
import com.example.buoi_02.repository.PermissionRepository;
import com.example.buoi_02.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final RoleMapper mapper;
	
	public RoleResponse create(RoleRequest request) {
		var role = mapper.toRole(request);
		
		var permissions = permissionRepository.findAllById(request.getPermissions());
		role.setPermissions(new HashSet<>(permissions));
		role = roleRepository.save(role);
		
		return mapper.toRoleResponse(role);
	}
	
	public List<RoleResponse> getAll() {
		return roleRepository.findAll()
				.stream()
				.map(mapper::toRoleResponse)
				.toList();
	}
	
	public void delete(String role) {
		roleRepository.deleteById(role);
	}
}
