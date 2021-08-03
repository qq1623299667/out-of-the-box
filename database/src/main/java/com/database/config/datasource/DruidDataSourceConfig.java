package com.database.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Druid数据源配置
 * 创建人：Will Shi <br>
 * 创建时间：2018-11-10 13:39 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Configuration
//扫描 Mapper 接口并容器管理
@MapperScan(basePackages = {DruidDataSourceConfig.PACKAGE1,
        DruidDataSourceConfig.PACKAGE2, DruidDataSourceConfig.PACKAGE3,DruidDataSourceConfig.PACKAGE4,DruidDataSourceConfig.PACKAGE5 },
        sqlSessionFactoryRef = "mysqlSessionFactory")
@Slf4j
public class DruidDataSourceConfig {

    /**
     * 精确到 master 目录，以便跟其他数据源隔离
     */
    static final String PACKAGE1 = "com.database.dataprocessing.mapper";
    static final String PACKAGE2 = "com.database.systemmanage.usermanage.mapper";
    static final String PACKAGE3 = "com.database.systemmanage.operationlog.mapper";
    static final String PACKAGE4 = "com.database.systemmanage.eventlog.mapper";
    static final String PACKAGE5 = "com.database.patient.mapper";
    private static final String MAPPER_LOCATION = "classpath*:/mappers/*/*.xml";

    @Value("${sql1.datasource.url}")
    private String url;
    @Value("${sql1.datasource.username}")
    private String user;
    @Value("${sql1.datasource.password}")
    private String password;
    @Value("${sql1.datasource.driverClassName}")
    private String driverClass;

    @Value("${sql1.datasource.maxActive}")
    private Integer maxActive;
    @Value("${sql1.datasource.minIdle}")
    private Integer minIdle;
    @Value("${sql1.datasource.initialSize}")
    private Integer initialSize;
    @Value("${sql1.datasource.maxWait}")
    private Long maxWait;
    @Value("${sql1.datasource.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;
    @Value("${sql1.datasource.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;
    @Value("${sql1.datasource.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${sql1.datasource.testWhileIdle}")
    private Boolean testOnBorrow;
    @Value("${sql1.datasource.testOnBorrow}")
    private Boolean testOnReturn;
    @Value("${spring.datasource.druid.filters}")
    private String filters;
    @Value("${spring.datasource.druid.connectionProperties}")
    private String connectionProperties;

    @Bean(name = "mysqlDataSource")
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    //@DependsOn("createDB")
    public DataSource sql1DataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(this.url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        //连接池配置
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");

        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        /*
          这个是用来配置 druid 监控sql语句的 非常有用 如果你有两个数据源 这个配置哪个数据源就监控哪个数据源的sql 同时配置那就都监控
         */
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        dataSource.setConnectionProperties(connectionProperties);

        return dataSource;
    }

    @Bean(name = "mysqlSessionFactory")
    @Primary
    public SqlSessionFactory sql1SqlSessionFactory(@Qualifier("mysqlDataSource") DataSource mysqlDataSource)
            throws Exception {
       final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(mysqlDataSource);
        sessionFactory.setTypeAliasesPackage("com.database.*.domain.po");
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));

        // Mybatis-plus 分页查询设置
       /* Interceptor[] plugins = new Interceptor[1];
        plugins[0] = MpPaginationInterceptor();
        sessionFactory.setPlugins(plugins);*/

/*
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        Interceptor interceptor = new PageInterceptor();
        interceptor.setProperties(properties);
        sessionFactory.setPlugins(new Interceptor[] {interceptor});*/
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MybatisObjectHandler());
        sessionFactory.setGlobalConfig(globalConfig);
        /*sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DruidDataSourceConfig.MAPPER_LOCATION));*/
        sessionFactory.setMapperLocations(resolveMapperLocations());
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));//分页插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());   //乐观锁

        sessionFactory.setPlugins(interceptor);
        return sessionFactory.getObject();
    }



   /**
    *
    * @Description:   myBatis-plus 分页查询插件配置
    * @param:
    * @return: com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor
    * @Author: Will Shi
    * @Date: 2021/3/2
    * @since: 1.0.0
    */
   /* @Bean(name="MpPaginationInterceptor")
    public PaginationInterceptor MpPaginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(300);
        paginationInterceptor.setDialectType(DbType.MYSQL.getDb());
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize());
        return paginationInterceptor;
    }*/

   /*
    *
    * @Description: 制定mapper.xml 路径
    * @param:
    * @return: org.springframework.core.io.Resource[]
    * @Author: Will Shi
    * @Date: 2021/4/25
    * @since: 1.0.0
    */
   public Resource[] resolveMapperLocations() {
       ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
       List<String> mapperLocations = new ArrayList<>();
       mapperLocations.add("classpath:mappers/*.xml");
       mapperLocations.add("classpath:mappers/systemmanage/*.xml");
       List<Resource> resources = new ArrayList();
       if (mapperLocations != null) {
           for (String mapperLocation : mapperLocations) {
               try {
                   Resource[] mappers = resourceResolver.getResources(mapperLocation);
                   resources.addAll(Arrays.asList(mappers));
               } catch (IOException e) {
                   // ignore
               }
           }
       }
       return resources.toArray(new Resource[resources.size()]);
   }


}
