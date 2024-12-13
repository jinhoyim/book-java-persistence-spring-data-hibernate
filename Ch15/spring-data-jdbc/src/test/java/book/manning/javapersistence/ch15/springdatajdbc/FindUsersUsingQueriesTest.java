package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FindUsersUsingQueriesTest extends SpringDataJdbcApplicationTests {

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(10);
    }

    @Test
    void testFindUser() {
        User beth = userRepository.findByUsername("beth").get();
        assertThat(beth.getUsername()).isEqualTo("beth");
    }

    @Test
    void testFindAllByOrderByUsernameAsc() {
        List<User> users = userRepository.findAllByOrderByUsernameAsc();
        assertAll(
                () -> assertThat(users).hasSize(10),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("beth"),
                () -> assertThat(users.get(users.size() - 1).getUsername()).isEqualTo("stephanie")
        );
    }

    @Test
    void testFindByRegistrationDateBetween() {
        List<User> users = userRepository.findByRegistrationDateBetween(
                LocalDate.of(2020, Month.JULY, 1),
                LocalDate.of(2020, Month.DECEMBER, 31));
        assertThat(users).hasSize(4);
    }

    @Test
    void testFindByUsernameEmail() {
        List<User> usersList1 = userRepository.findByUsernameAndEmail("mike", "mike@somedomain.com");
        List<User> usersList2 = userRepository.findByUsernameOrEmail("mike", "beth@somedomain.com");
        List<User> usersList3 = userRepository.findByUsernameAndEmail("mike", "beth@somedomain.com");
        List<User> usersList4 = userRepository.findByUsernameOrEmail("beth", "beth@somedomain.com");

        assertAll(
                () -> assertThat(usersList1).hasSize(1),
                () -> assertThat(usersList2).hasSize(2),
                () -> assertThat(usersList3).hasSize(0),
                () -> assertThat(usersList4).hasSize(1)
        );
    }

    @Test
    void testFindByUsernameIgnoreCase() {
        List<User> users = userRepository.findByUsernameIgnoreCase("MIKE");

        assertAll(
                () -> assertThat(users).hasSize(1),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("mike")
        );
    }

    @Test
    void testFindByLevelOrderByUsernameDesc() {
        List<User> users = userRepository.findByLevelOrderByUsernameDesc(1);

        assertAll(
                () -> assertThat(users).hasSize(2),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("john"),
                () -> assertThat(users.get(1).getUsername()).isEqualTo("burk")
        );
    }

    @Test
    void testFindByLevelGreaterThanEqual() {
        List<User> users = userRepository.findByLevelGreaterThanEqual(3);

        assertThat(users).hasSize(5);
    }

    @Test
    void testFindByUsername() {
        List<User> usersContaining = userRepository.findByUsernameContaining("ar");
        List<User> usersLike = userRepository.findByUsernameLike("%ar%");
        List<User> usersStarting = userRepository.findByUsernameStartingWith("b");
        List<User> usersEnding = userRepository.findByUsernameEndingWith("ie");

        assertAll(
                () -> assertThat(usersContaining).hasSize(2),
                () -> assertThat(usersLike).hasSize(2),
                () -> assertThat(usersStarting).hasSize(2),
                () -> assertThat(usersEnding).hasSize(2)
        );
    }

    @Test
    void testFindByActive() {
        List<User> usersActive = userRepository.findByActive(true);
        List<User> usersNotActive = userRepository.findByActive(false);

        assertAll(
                () -> assertThat(usersActive).hasSize(8),
                () -> assertThat(usersNotActive).hasSize(2)
        );
    }

    @Test
    void testFindByRegistrationDateInNotIn() {
        LocalDate date1 = LocalDate.of(2020, Month.JANUARY, 18);
        LocalDate date2 = LocalDate.of(2021, Month.JANUARY, 5);

        List<LocalDate> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);

        List<User> usersList1 = userRepository.findByRegistrationDateIn(dates);
        List<User> usersList2 = userRepository.findByRegistrationDateNotIn(dates);

        assertAll(
                () -> assertThat(usersList1).hasSize(3),
                () -> assertThat(usersList2).hasSize(7)
        );
    }
}
