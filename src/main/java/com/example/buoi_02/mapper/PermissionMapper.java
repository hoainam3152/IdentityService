package com.example.buoi_02.mapper;

import org.mapstruct.Mapper;
import com.example.buoi_02.dto.request.PermissionRequest;
import com.example.buoi_02.dto.response.PermissionResponse;
import com.example.buoi_02.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
	Permission toPermission(PermissionRequest request);
	PermissionResponse toPermissionResponse(Permission permission);
}
