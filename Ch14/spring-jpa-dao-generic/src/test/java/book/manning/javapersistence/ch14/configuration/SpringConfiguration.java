package book.manning.javapersistence.ch14.configuration;

import book.manning.javapersistence.ch14.Bid;
import book.manning.javapersistence.ch14.DatabaseService;
import book.manning.javapersistence.ch14.Item;
import book.manning.javapersistence.ch14.dao.BidDaoImpl;
import book.manning.javapersistence.ch14.dao.GenericDao;
import book.manning.javapersistence.ch14.dao.ItemDaoImpl;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
public class SpringConfiguration {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/CH14_SPRING_HIBERNATE?serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword(System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        return dataSource;
    }

    @Bean
    public DatabaseService databaseService() {
        return new DatabaseService();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean =
                new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("ch14");
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("book.manning.javapersistence.ch14");
        return factoryBean;
    }

    @Bean
    public GenericDao<Item> itemDao() {
        return new ItemDaoImpl();
    }

    @Bean
    public GenericDao<Bid> bidDao() {
        return new BidDaoImpl();
    }
}
