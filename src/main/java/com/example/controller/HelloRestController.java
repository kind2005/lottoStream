package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dao.HelloDao;
import com.example.model.Hello;

@RestController
public class HelloRestController {
	@Autowired
	private HelloDao helloDao;
	
	@RequestMapping("/add")
	public Hello add(Hello hello) {

		Hello helloData = helloDao.save(hello);

		return helloData;
	}

	@RequestMapping("/list")
	public List<Hello> list(Model model) {

		List<Hello> helloList = helloDao.findAll();

		return helloList;
	}

	@RequestMapping("/")
	public String index() {
		return "helloworld!";
	}
}
