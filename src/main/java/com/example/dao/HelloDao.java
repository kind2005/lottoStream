package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Hello;

public interface HelloDao extends JpaRepository<Hello, Integer>{

}
