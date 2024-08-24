package com.example.buoi_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.buoi_02.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{
	
}
