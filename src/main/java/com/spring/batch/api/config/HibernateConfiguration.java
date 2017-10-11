package com.spring.batch.api.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@PropertySource(value = { "classpath:db.properties" })
public class HibernateConfiguration {

	private Logger log = LoggerFactory.getLogger(HibernateConfiguration.class);
	@Autowired
	private Environment environment;
	@Value("${jdbc.url}")
	private String url;

	@Bean
	public DataSource dataSource() {
		log.info("url {}", url);
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment
				.getRequiredProperty("jdbc.driverClassName"));
		dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
		dataSource
				.setUsername(environment.getRequiredProperty("jdbc.username"));
		dataSource
				.setPassword(environment.getRequiredProperty("jdbc.password"));
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory
				.setPackagesToScan(new String[] { "com.spring.batch.api.model" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect",
				environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.hbm2ddl.auto",
				environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
		properties.put("hibernate.show_sql",
				environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql",
				environment.getRequiredProperty("hibernate.format_sql"));
		return properties;
	}

	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(s);
		return transactionManager;
	}

}
