package com.example.buoi_02.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
	@Id
	private String name;
	private String description;
	
	@ManyToMany //biểu diễn mối quan hệ nhiều nhiều
	Set<Permission> permissions;
}
