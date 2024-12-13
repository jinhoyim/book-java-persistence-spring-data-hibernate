package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FindUsersSortingAndPagingTest extends SpringDataJdbcApplicationTests {

    @Test
    void testOrder() {

        User user1 = userRepository.findFirstByOrderByUsernameAsc().get();
        User user2 = userRepository.findTopByOrderByRegistrationDateDesc().get();
//        Page<User> userPage = userRepository.findTop3ByActive(true, PageRequest.of(1, 3));
        Page<User> userPage = userRepository.findAll(PageRequest.of(1, 3));
        List<User> users = userRepository.findFirst2ByLevel(2, Sort.by("registrationDate"));

        assertAll(
                () -> assertThat(user1.getUsername()).isEqualTo("beth"),
                () -> assertThat(user2.getUsername()).isEqualTo("julius"),
                () -> assertThat(users).hasSize(2),
                () -> assertThat(userPage).hasSize(3),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("beth"),
                () -> assertThat(users.get(1).getUsername()).isEqualTo("marion")
        );
    }

    @Test
    void testFindByLevel() {
        Sort.TypedSort<User> user = Sort.sort(User.class);

        List<User> users = userRepository.findByLevel(3, user.by(User::getRegistrationDate).descending());
        assertAll(
                () -> assertThat(users).hasSize(2),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("james")
        );
    }

    @Test
    void testFindByActive() {
        List<User> users = userRepository.findByActive(true, PageRequest.of(1, 4, Sort.by("registrationDate")));
        assertAll(
                () -> assertThat(users).hasSize(4),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("burk")
        );
    }
}
