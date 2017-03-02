package me.lb.service.demo.impl;

import me.lb.model.demo.Foo;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.demo.FooService;

import org.springframework.stereotype.Service;

@Service
public class FooServiceImpl extends GenericServiceImpl<Foo> implements
		FooService {

}
