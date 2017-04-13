package me.lb.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafController {

	@RequestMapping(value = "/test")
	public ModelAndView test() {
		ModelAndView mv = new ModelAndView("/thymeleaf/test");
		mv.addObject("hello", "Hello World!");
		return mv;
	}

}