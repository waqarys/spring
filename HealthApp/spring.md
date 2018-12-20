# Configuring bean definitions
```aidl
@Configuration
@EntityScan("com.book.healthapp.domain")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class AppConfig { 
    ... 
}
```

**Data Source**
```aidl
@Value("${spring.datasource.driverClassName}") String    
  driverClassName;
 @Value("${spring.datasource.url}") String url;
 @Value("${spring.datasource.username}") String username;
 @Value("${spring.datasource.password}") String password;
 
 @Bean(name = "dataSource")
 public DataSource getDataSource() {
   DataSource dataSource = DataSourceBuilder
    .create()
    .username(username)
    .password(password)
    .url(url)
    .driverClassName(driverClassName)
    .build();
   return dataSource;
 }
```

**Session Factory**
```aidl
@Bean(name = "sessionFactory")
public SessionFactory getSessionFactory(DataSource dataSource) {
  LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
  sessionBuilder.scanPackages("com.book.healthapp.domain");
  return sessionBuilder.buildSessionFactory();
}
```

**Hibernate Transaction Manager**
```aidl
@Bean(name = "transactionManager")
public HibernateTransactionManager getTransactionManager(
  SessionFactory sessionFactory) {
   HibernateTransactionManager transactionManager = new    
     HibernateTransactionManager(sessionFactory);
   return transactionManager;
}
```

