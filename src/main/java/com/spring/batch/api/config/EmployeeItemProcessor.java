package com.spring.batch.api.config;

import org.springframework.batch.item.ItemProcessor;

import com.spring.batch.api.model.Employee;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(Employee employee) throws Exception {
		return employee;
	}

}
