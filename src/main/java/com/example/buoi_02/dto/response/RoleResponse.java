package com.example.buoi_02.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor		//tạo 1 constructor với không tham số
@AllArgsConstructor		//tạo 1 constructor với tất cả tham số
@Builder
public class RoleResponse {
	private String name;
	private String description;
	Set<PermissionResponse> permissions; 
}
