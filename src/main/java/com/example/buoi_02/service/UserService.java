package com.example.buoi_02.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.buoi_02.dto.request.UserCreationRequest;
import com.example.buoi_02.dto.request.UserUpdateRequest;
import com.example.buoi_02.dto.response.UserResponse;
import com.example.buoi_02.entity.User;
import com.example.buoi_02.enums.Role;
import com.example.buoi_02.exception.AppException;
import com.example.buoi_02.exception.ErrorCode;
import com.example.buoi_02.mapper.UserMapper;
import com.example.buoi_02.repository.RoleRepository;
import com.example.buoi_02.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor	//tạo 1 constructor chứa tất các biến có khoá final và inject những cái bean vào
@Slf4j
public class UserService {

	private final UserRepository userRepository;	//dùng thêm @FielDefaults
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;			//dùng thêm @FielDefaults
	private final PasswordEncoder passwordEncoder;

	public UserResponse createUser(UserCreationRequest request) {
//		if (userRepository.existsByUserName(request.getUserName())) {
//			throw new RuntimeException("ErrorCode.USER_EXISTED");
//		}

		if (userRepository.existsByUserName(request.getUserName())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}

		//thay vì tạo đối tượng và set giá trị như user1
		//thì annotation @Builder giúp tạo 1 đối tượng nhanh hơn và clean hơn
//		UserCreationRequest user1 = new UserCreationRequest();
//		user1.setUserName("abc");
//		user.setPassword("123");
		//annotation @Builder
//		UserCreationRequest userBuilder = UserCreationRequest.builder()
//				.userName("abc")
//				.password("123")
//				.build();

		//annatation @Mapper được sử dụng để ánh xạ giữa các đối tượng có cấu trúc phức tạp
		//Thay vì phải set từng giá trị cho từng field của đối tượng
		//Thì @Mapper sẽ điều đó giúp code trở nên gọn hơn
//		User user = new User();
//		user.setUserName(request.getUserName());
//		user.setPassword(request.getPassword());
//		user.setFirstName(request.getFirstName());
//		user.setLastName(request.getLastName());
//		user.setBirthday(request.getBirthday());
		User user = userMapper.toUser(request);
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.USER.name());
//		user.setRoles(roles);

		return userMapper.toUserResponse(userRepository.save(user));
	}

	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		userMapper.updateUser(user, request);
//		user.setPassword(request.getPassword());
//		user.setFirstName(request.getFirstName());
//		user.setLastName(request.getLastName());
//		user.setBirthday(request.getBirthday());
		var roles = roleRepository.findAllById(request.getRoles());
		user.setRoles(new HashSet<>(roles));
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		return userMapper.toUserResponse(userRepository.save(user));
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	//để phương thức getUsers chỉ cho phép những user nào có role admin 
	//mới được truy cập vào thì dùng annotation @PreAuthorize
	//tạo ra 1 AOP bọc bên ngoài phương thức, nếu không thoả điều kiện thì phương này không được gọi
//	@PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasAuthority('UPDATE_DATA')")
	public List<UserResponse> getUsers() {
		log.info("In method get Users");
		return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
	}

	//ngược lại với @PreAuthorize
	@PostAuthorize("returnObject.userName == authentication.name")
	public UserResponse getUser(String id) {
		log.info("In method get User by Id");
		return userMapper.toUserResponse(userRepository
					.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found"))
				);
	}
	
	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		User user = userRepository.findByUserName(name).orElseThrow(
					() -> new AppException(ErrorCode.USER_NOT_EXISTED)
				);
		
		return userMapper.toUserResponse(user);
	}
}
