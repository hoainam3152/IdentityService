package com.example.buoi_02.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.buoi_02.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String>{

}
