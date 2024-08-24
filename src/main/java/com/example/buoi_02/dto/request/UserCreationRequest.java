package com.example.buoi_02.dto.request;

import java.time.LocalDate;

import com.example.buoi_02.validatior.DobConstraint;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {

	@Size(min = 4, message = "USERNAME_INVALID")
    private String userName;

    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

    private String firstName;
    private String lastName;

    @DobConstraint(min = 16, message = "INVALID_DOB")
    private LocalDate birthday;

}