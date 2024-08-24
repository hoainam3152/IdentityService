package com.example.buoi_02.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.buoi_02.dto.request.RoleRequest;
import com.example.buoi_02.dto.response.RoleResponse;
import com.example.buoi_02.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	@Mapping(target = "permissions", ignore = true)
	Role toRole(RoleRequest request);
	RoleResponse toRoleResponse(Role role);
}
