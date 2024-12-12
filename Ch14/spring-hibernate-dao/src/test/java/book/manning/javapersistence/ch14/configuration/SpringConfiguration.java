package book.manning.javapersistence.ch14.configuration;

import book.manning.javapersistence.ch14.DatabaseService;
import book.manning.javapersistence.ch14.dao.BidDao;
import book.manning.javapersistence.ch14.dao.BidDaoImpl;
import book.manning.javapersistence.ch14.dao.ItemDao;
import book.manning.javapersistence.ch14.dao.ItemDaoImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement
public class SpringConfiguration {

    @Bean
    LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("book.manning.javapersistence.ch14");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, "create");
        properties.setProperty(AvailableSettings.SHOW_SQL, "true");
        properties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
        return properties;
    }

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/CH14_SPRING_HIBERNATE?serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword(System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        return dataSource;
    }

    @Bean
    DatabaseService databaseService() {
        return new DatabaseService();
    }

    @Bean
    HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    ItemDao itemDao(SessionFactory sessionFactory) {
        return new ItemDaoImpl(sessionFactory);
    }

    @Bean
    BidDao bidDao(SessionFactory sessionFactory) {
        return new BidDaoImpl(sessionFactory);
    }
}
