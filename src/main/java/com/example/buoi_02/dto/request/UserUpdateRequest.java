package com.example.buoi_02.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.example.buoi_02.validatior.DobConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
	private String password;
	private String firstName;
	private String lastName;
	@DobConstraint(min = 18, message = "INVALID_DOB")
	private LocalDate birthday;
	private List<String> roles;
}
