package me.lb.service.demo.impl;

import org.springframework.stereotype.Service;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.demo.FooService;
import me.lb.model.demo.Foo;

@Service
public class FooServiceImpl extends GenericServiceImpl<Foo, Integer> implements
		FooService {

}
