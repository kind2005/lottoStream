package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/index")
	public String index(Model model) {

		model.addAttribute("name", "helloworld!");

		return "index";
	}

	@RequestMapping("/calcLot")
	public String calcLot(Model model) {

		model.addAttribute("name", "helloworld1!");

		return "index";
	}

	@RequestMapping("/pollingTest")
	public String pollingTest() throws Exception{

			return "/coin/pollingTest";
	}

}
