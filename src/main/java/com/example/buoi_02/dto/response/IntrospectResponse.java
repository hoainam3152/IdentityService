package com.example.buoi_02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor		//tạo 1 constructor với không tham số
@AllArgsConstructor		//tạo 1 constructor với tất cả tham số
@Builder
public class IntrospectResponse {
	private boolean valid;
}
