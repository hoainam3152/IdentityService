package com.example.buoi_02.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.buoi_02.dto.request.UserCreationRequest;
import com.example.buoi_02.dto.request.UserUpdateRequest;
import com.example.buoi_02.dto.response.UserResponse;
import com.example.buoi_02.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toUser(UserCreationRequest request);
	//@Mapping trên dùng để map 2 đối tượng khác nhau
	//source: là nơi để map về
	//target: dùng để map từ target về source
	//ignore: có cho phép mapping hay không
//	@Mapping(source = "firstName", target = "lastName")	//map lastName vào firstName
//	@Mapping(target = "lastName", ignore = true)		//cho phép map tất cả trừ lastName
	UserResponse toUserResponse(User user);
	@Mapping(target = "roles", ignore = true)
	void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
