/**
* @author ZhangHongdong
* @date 2015年8月27日-上午10:42:46
* @see (参阅)
*/
package cn.signit.server.conf;


import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.alibaba.druid.pool.DruidDataSource;

import cn.signit.conf.ConfigProps;

/**
*基于注解的web mvc上下文Model对象数据持久化（Spring MVC Context）配置
*
* @ClassName JPAConfig
* @author ZhangHongdong
* @date 2015年8月27日-上午10:42:46
* @version 1.1.0
*/
@Configuration
@ComponentScan(BaseConfig.BASE_PKG_NAME+"**.domain.mysql")
//@MapperScan(basePackages={"cn.signit.dao"},sqlSessionFactoryRef="sqlSessionFactory")
//启用切面
@Aspect
//导入xml配置资源
@ImportResource({
			"classpath:xml/aop-config.xml",//用于切面的事务配置
})
public class DataConfig{
	private final static Logger LOG = LoggerFactory.getLogger(DataConfig.class);
	
	/**
	 * +--------------------------------------------------------+
	 * |																		|
	 * |基于MySql的数据库配置								|
	 * |																		|
	 * |用途：核心业务系统										|
	 * |																		|
	 * +--------------------------------------------------------+
	 */
	
	
	/**
	* 配置数据源（采用DRUID数据源）
	* 
	* @return 名称为"dataSource"的数据源bean
	*/
	@Bean(initMethod="init",destroyMethod="close")
	public DataSource dataSource() {
		//LOG.info("======================>>  配置数据源（采用DRUID数据源）( {} ) ",DruidDataSource.class.getName());
		DruidDataSource dataSource = new DruidDataSource(true);
		/*数据库连接*/
		dataSource.setUrl(ConfigProps.get("db.mysql.jdbc_url"));
		dataSource.setDriverClassName(ConfigProps.get("db.mysql.jdbc_driver"));
		dataSource.setUsername(ConfigProps.get("db.mysql.jdbc_user"));
		dataSource.setPassword(ConfigProps.get("db.mysql.jdbc_password"));
		/*连接配置*/
		//连接池最大连接数
		dataSource.setMaxActive(20);
		//连接池最大等待时间
		dataSource.setMaxWait(60000);
		//数据库测试使用
		dataSource.setValidationQuery(ConfigProps.get("db.mysql.validation_query"));
		//其他配置采用默认
		
		return dataSource;
	}
	/**
	*配事务管理器
	*
	*@return 名称为"transactionManager"的数据源方式的事务管理bean
	*/
	@Bean
	public DataSourceTransactionManager transactionManager() {
		LOG.info("======================>>  配事务管理器 ( {} ) ",DataSourceTransactionManager.class.getName());
		DataSourceTransactionManager dstm = new DataSourceTransactionManager(dataSource());
		return dstm;
	}

	/**
	* 创建基于MyBatis的SqlSessionFactory（连接池），同时指定其使用的数据源
	* 
	*@return 名称为"sqlSessionFactory"的Mybatis SqlSessionFactory连接池bean
	*
	*@throws Exception
	*/
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		LOG.info("======================>>  创建基于MyBatis的SqlSessionFactory（连接池），同时指定其使用的数据源 ( {} ) ",SqlSessionFactoryBean.class.getName());
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		//自动扫描domain目录，省掉手动配置Configuration.xml
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:cn/signit/mappingXML/*.xml"));
		sessionFactory.setTypeAliasesPackage("cn.signit.domain");//不支持ANT风格路径，只能用",;\t\n"分隔
		return sessionFactory.getObject();
	}
	
	/**
	* 自动扫描将Mapper接口生成代理注入到Spring容器
	* 
	*@return 名称为"mapperScanner"的Mapper接口扫描器bean
	 * @throws Exception 
	*/
	@Bean
	public MapperScannerConfigurer mapperScanner() throws Exception{
		LOG.info("======================>>  自动扫描将Mapper接口生成代理注入到Spring容器 ( {} ) ",MapperScannerConfigurer.class.getName());
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("cn.signit.dao.mysql");
		msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return msc;
	}

	/**
	* 以拦截器方式配置事务，将所有Service纳入事务管理
	*/
	@Pointcut("execution(* cn.signit.service..*.*(..))")
    public void serviceAnnotatedClass() {
		LOG.info("======================>>  以拦截器方式配置事务，将所有Service纳入事务管理");
	}
	
	
}
