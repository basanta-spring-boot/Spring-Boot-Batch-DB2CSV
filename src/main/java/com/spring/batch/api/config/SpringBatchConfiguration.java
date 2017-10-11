package com.spring.batch.api.config;

import javax.transaction.Transactional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import com.spring.batch.api.model.Employee;

@SpringBootApplication
@EnableBatchProcessing
@Import({ HibernateConfiguration.class })
@Transactional
public class SpringBatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Autowired
	private HibernateConfiguration conf;

	@Bean
	public HibernateCursorItemReader<Employee> reader() {
		HibernateCursorItemReader<Employee> reader = new HibernateCursorItemReader<Employee>();
		reader.setSessionFactory(conf.sessionFactory().getObject());
		reader.setQueryString("from Employee e");
		return reader;
	}

	/*
	 * This is how we can use JDBC as well public JdbcCursorItemReader<Employee>
	 * reader(){ JdbcCursorItemReader<Employee> reader=new
	 * JdbcCursorItemReader<Employee>();
	 * reader.setDataSource(conf.dataSource());
	 * reader.setSql("select * from EMP_BATCH_RECORD"); reader.setRowMapper(new
	 * BeanPropertyRowMapper<Employee>(Employee.class)); return reader; }
	 */
	@Bean
	public ItemProcessor<Employee, Employee> processor() {
		return new EmployeeItemProcessor();
	}

	@Bean
	public FlatFileItemWriter<Employee> writer() {
		FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<Employee>();
		writer.setResource(new ClassPathResource("employee.csv"));
		writer.setLineAggregator(new DelimitedLineAggregator<Employee>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<Employee>() {
					{
						setNames(new String[] { "id", "name", "salary", "age" });
					}
				});
			}
		});
		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, Employee> chunk(10)
				.reader(reader()).processor(processor()).writer(writer())
				.build();
	}

	@Bean
	public Job proceedJob() {
		return jobBuilderFactory.get("exportEmployeeData")
				.incrementer(new RunIdIncrementer()).flow(step1()).end()
				.build();
	}
}
