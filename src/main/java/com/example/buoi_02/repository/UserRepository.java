package com.example.buoi_02.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.buoi_02.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

	boolean existsByUserName(String userName);
	Optional<User> findByUserName(String userName);

}
