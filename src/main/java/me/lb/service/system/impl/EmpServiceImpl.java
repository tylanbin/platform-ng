package me.lb.service.system.impl;

import me.lb.model.system.Emp;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.EmpService;

import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl extends GenericServiceImpl<Emp, Integer> implements
		EmpService {

}