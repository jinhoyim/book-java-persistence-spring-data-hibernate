package book.manning.javapersistence.ch15.springdatajdbc;

import book.manning.javapersistence.ch15.springdatajdbc.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ModifyQueryTest extends SpringDataJdbcApplicationTests {

    @Test
    void testModifyLevel() {
        int updated = userRepository.updateLevel(5, 4);
        List<User> users = userRepository.findByLevel(4, Sort.by("username"));

        assertAll(
                () -> assertThat(updated).isEqualTo(1),
                () -> assertThat(users).hasSize(3),
                () -> assertThat(users.get(1).getUsername()).isEqualTo("katie")
        );
    }
}
