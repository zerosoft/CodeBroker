package com.codebroker.component.service;


import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import com.codebroker.component.BaseCoreService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.base.Strings;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3P0的数据库连接池，为Mybatis使用的
 * 
 * @author zhaoxiaolong
 * 
 */
public class DataSourceComponent extends BaseCoreService {

	private Map<String,DataSource> dataSourceMap=new HashMap<>();

	@Override
	public String getName() {
		return DataSourceComponent.class.getName();
	}

	@Override
	public void init(Object obj) {
		PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
		List<String> listProperty = propertiesWrapper.getListProperty(SystemEnvironment.MYSQL_SOURCE_NAME, String.class, "");
		listProperty.stream().filter(st-> !Strings.isNullOrEmpty(st)).forEach(key->{
			ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
			String mysqlDriver = String.format(SystemEnvironment.MYSQL_DRIVER, key);
			String mysqlUrl = String.format(SystemEnvironment.MYSQL_URL, key);
			String mysqlUserName = String.format(SystemEnvironment.MYSQL_USER_NAME, key);
			String mysqlPassWord = String.format(SystemEnvironment.MYSQL_PASS_WORD, key);

			String minPoolSize = String.format(SystemEnvironment.MYSQL_MIN_POOL_SIZE, key);
			String maxPoolSize = String.format(SystemEnvironment.MYSQL_MAX_POOL_SIZE, key);
			String acquireIncrement = String.format(SystemEnvironment.MYSQL_ACQUIRE_INCREMENT, key);


			try {
				comboPooledDataSource.setDriverClass(propertiesWrapper.getProperty(mysqlDriver));
				comboPooledDataSource.setJdbcUrl(propertiesWrapper.getProperty(mysqlUrl));
				comboPooledDataSource.setUser(propertiesWrapper.getProperty(mysqlUserName));
	     		comboPooledDataSource.setPassword(propertiesWrapper.getProperty(mysqlPassWord));

				comboPooledDataSource.setAcquireIncrement(propertiesWrapper.getIntProperty(acquireIncrement,5));
				comboPooledDataSource.setMinPoolSize(propertiesWrapper.getIntProperty(minPoolSize,10));
	            comboPooledDataSource.setMaxPoolSize(propertiesWrapper.getIntProperty(maxPoolSize,20));

	            dataSourceMap.put(key,comboPooledDataSource);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		});
		setActive();
	}

	public Optional<Connection> getConnect(String key){
		DataSource dataSource = dataSourceMap.get(key);
		Connection connection;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			return Optional.empty();
		}
		return Optional.ofNullable(connection);
	}

	public Optional<DataSource> getDataSource(String key) {
		return Optional.ofNullable(dataSourceMap.get(key));
	}
}
