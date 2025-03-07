package book.manning.javapersistence.springdatajpa;

import book.manning.javapersistence.springdatajpa.model.User;
import book.manning.javapersistence.springdatajpa.repositories.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

    @Bean
    public ApplicationRunner configure(UserRepository userRepository) {
        return env -> {
//            User user1 = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
//            User user2 = new User("mike", LocalDate.of(2020, Month.JANUARY, 21));
//
//            userRepository.save(user1);
//            userRepository.save(user2);

            Iterable<User> all = userRepository.findAll();
            all.forEach(System.out::println);
        };
    }
}
