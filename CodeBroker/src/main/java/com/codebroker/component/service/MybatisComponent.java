package com.codebroker.component.service;


import com.codebroker.component.BaseCoreService;
import com.codebroker.core.ContextResolver;

import com.codebroker.core.ServerEngine;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jodd.io.FileUtil;
import jodd.io.findfile.ClassScanner;
import jodd.io.findfile.FindFile;
import jodd.util.ClassLoaderUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * C3P0的数据库连接池，为Mybatis使用的
 * 
 * @author zhaoxiaolong
 * 
 */
public class MybatisComponent extends BaseCoreService {

	private Logger logger= LoggerFactory.getLogger(MybatisComponent.class);

	private Map<String, SqlSessionFactory> sqlSessionFactoryMap = Maps.newConcurrentMap();

	@Override
	public void init(Object obj) {
		PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
		List<String> listProperty = propertiesWrapper.getListProperty(SystemEnvironment.MYSQL_SOURCE_NAME, String.class, "");
		listProperty.stream().filter(db-> !Strings.isNullOrEmpty(db)).forEach(dbName->{
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Optional<DataSourceComponent> component = ContextResolver.getComponent(DataSourceComponent.class);
			DataSource dataSource=null;
			if (component.isPresent()){
				Optional<DataSource> dataSourceOptional = component.get().getDataSource(dbName);
				if (dataSourceOptional.isPresent()){
					dataSource=dataSourceOptional.get();

					Environment environment = new Environment(dbName, transactionFactory, dataSource);
					Configuration configuration = new Configuration(environment);

					configuration.setLazyLoadingEnabled(true);


					String path = propertiesWrapper.getProperty(SystemEnvironment.APP_JAR_PATH);
					String modelPackage = propertiesWrapper.getProperty(String.format(SystemEnvironment.MYBATIS_MODEL, dbName));
					String mapperPackage = propertiesWrapper.getProperty(String.format(SystemEnvironment.MYBATIS_MAPPER, dbName));

					org.apache.ibatis.io.Resources.setDefaultClassLoader(ServerEngine.getInstance().getiClassLoader());

					List<File> files = FindFile.create().searchPath(path).findAll();
					for (File file : files) {
						ClassScanner classScanner = new ClassScanner();
						classScanner.registerEntryConsumer(entryData ->
						{
							if (entryData.name().startsWith(modelPackage)) {
								if (entryData.name().contains("Example") || entryData.name().contains("$")) {
									//过滤掉非相关对象
									return;
								}
								try {
									Class aClass = ClassLoaderUtil.loadClass(entryData.name(), ServerEngine.getInstance().getiClassLoader());
									configuration.getTypeAliasRegistry().registerAlias(aClass);
									logger.info("find mybatis model {}",aClass);
								} catch (ClassNotFoundException e) {
									logger.error("find mybatis model error",e);
								}
							}else if (entryData.name().startsWith(mapperPackage)){
								try {
									Class aClass = ClassLoaderUtil.loadClass(entryData.name(), ServerEngine.getInstance().getiClassLoader());
									configuration.addMapper(aClass);
									logger.info("find mybatis mapper {}",aClass);
								} catch (ClassNotFoundException e) {
									logger.error("find mybatis model mapper",e);
								}
							}
						}).scan(file).start();
					}

					String XMLPath =  propertiesWrapper.getProperty(String.format(SystemEnvironment.MYBATIS_XML_PATH, dbName));
					FindFile findFile = new FindFile().searchPath(XMLPath);
					findFile.forEach(file -> {
						try {
							logger.info("find mybatis xml resource {}",file.getName());
							String URI = file.toURI().toString();
							Map<String, XNode> sqlFragments = configuration.getSqlFragments();
							FileInputStream inputStream = new FileInputStream(file);
							new XMLMapperBuilder(inputStream, configuration, URI, sqlFragments).parse();
						} catch (FileNotFoundException e) {
							logger.error("find mybatis xml error",e);
						}
					});

					SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
					SqlSessionFactory sqlSessionFactory = builder.build(configuration);
					sqlSessionFactoryMap.put(dbName,sqlSessionFactory);
					logger.info("put {} session factory {}",dbName,sqlSessionFactory.toString());
				}
			}
		});
		setActive();
	}

	public Optional<SqlSessionFactory> getSqlSessionFactory(String sourceName){
		return Optional.ofNullable(sqlSessionFactoryMap.get(sourceName));
	}

}
