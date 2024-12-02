package book.jpa.jpa_hibernate.springdatajpa;

import book.jpa.jpa_hibernate.springdatajpa.model.User;
import book.jpa.jpa_hibernate.springdatajpa.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class SpringDataJpaApplicationTests {

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void beforeAll() {
        userRepository.saveAll(generateUsers());
    }

    private List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        User john = new User("John", LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@example.org");
        john.setLevel(1);
        john.setActive(true);
        users.add(john);

        User mike = new User("mike", LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@example.org");
        mike.setLevel(3);
        mike.setActive(true);
        users.add(mike);

        User james = new User("james", LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        users.add(james);

        User katie = new User("katie", LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        users.add(katie);

        User beth = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);
        users.add(beth);

        User julius = new User("julius", LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);
        users.add(julius);

        User darren = new User("darren", LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);
        users.add(darren);

        User marion = new User("marion", LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);
        users.add(marion);

        User stephanie = new User("stephanie", LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);
        users.add(stephanie);

        User burk = new User("burk", LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);
        users.add(burk);

        return users;
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
    }
}
