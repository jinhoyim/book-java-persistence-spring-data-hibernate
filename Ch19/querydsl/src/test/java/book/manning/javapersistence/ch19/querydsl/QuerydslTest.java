package book.manning.javapersistence.ch19.querydsl;

import book.manning.javapersistence.ch19.querydsl.configuration.SpringDataConfiguration;
import book.manning.javapersistence.ch19.querydsl.model.QBid;
import book.manning.javapersistence.ch19.querydsl.model.QUser;
import book.manning.javapersistence.ch19.querydsl.model.User;
import book.manning.javapersistence.ch19.querydsl.repositories.UserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static book.manning.javapersistence.ch19.querydsl.GenerateUsers.generateUsers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuerydslTest {

    private static EntityManagerFactory entityManagerFactory;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));
        entityManagerFactory = Persistence.createEntityManagerFactory("ch19.querydsl", properties);
    }

    @Autowired
    private UserRepository userRepository;

    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @BeforeAll
    void beforeAll() {
        userRepository.saveAll(generateUsers());
    }

    @BeforeEach
    void beforeEach() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @AfterEach
    void afterEach() {
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUsername() {
        User fetchedUser = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq("john"))
                .fetchOne();

        assertAll(
                () -> assertThat(fetchedUser).isNotNull(),
                () -> assertThat(fetchedUser.getUsername()).isEqualTo("john"),
                () -> assertThat(fetchedUser.getFirstName()).isEqualTo("John"),
                () -> assertThat(fetchedUser.getLastName()).isEqualTo("Smith"),
                () -> assertThat(fetchedUser.getBids()).hasSize(2)
        );
    }

    @Test
    void testByLevelAndActive() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.level.eq(3).and(QUser.user.active.eq(true)))
                .fetch();
        assertThat(users).hasSize(1);
    }

    // testByLevelAndActive() 와 동일한 쿼리 실행
    @Test
    void testByLevelAndActiveUncheckedCast() {
        List<User> users = (List<User>) queryFactory.from(QUser.user)
                .where(QUser.user.level.eq(3).and(QUser.user.active.eq(true)))
                .fetch();
        assertThat(users).hasSize(1);
    }

    @Test
    void testOrderByUsername() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .orderBy(QUser.user.username.asc())
                .fetch();

        assertAll(
                () -> assertThat(users).hasSize(10),
                () -> assertThat(users.get(0).getUsername()).isEqualTo("beth"),
                () -> assertThat(users.get(1).getUsername()).isEqualTo("burk"),
                () -> assertThat(users.get(8).getUsername()).isEqualTo("mike"),
                () -> assertThat(users.get(9).getUsername()).isEqualTo("stephanie")
        );
    }

    @Test
    void testGroupByBidAmount() {
        NumberPath<Long> count = Expressions.numberPath(Long.class, "bids");

        List<Tuple> userBidsGroupByAmount = queryFactory
                .select(QBid.bid.amount, QBid.bid.id.count().as(count))
                .from(QBid.bid)
                .groupBy(QBid.bid.amount)
                .orderBy(count.desc())
                .fetch();

        assertAll(
                () -> assertThat(userBidsGroupByAmount.get(0).get(QBid.bid.amount))
                        .isEqualTo(new BigDecimal("120.00")),
                () -> assertThat(userBidsGroupByAmount.get(0).get(count)).isEqualTo(2)
        );
    }

    @Test
    void testAggregateBidAmount() {
        JPAQuery<?> from = queryFactory.from(QBid.bid);

        assertAll(
                () -> assertThat(from.select(QBid.bid.amount.max()).fetchOne())
                        .isEqualTo(new BigDecimal("120.00")),
                () -> assertThat(from.select(QBid.bid.amount.min()).fetchOne())
                        .isEqualTo(new BigDecimal("100.00")),
                () -> assertThat(from.select(QBid.bid.amount.avg()).fetchOne())
                        .isEqualTo(112.6)
        );
    }

    @Test
    void testSubquery() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.id.in(
                        JPAExpressions.select(QBid.bid.user.id)
                                .from(QBid.bid)
                                .where(QBid.bid.amount.eq(new BigDecimal("120.00")))))
                .fetch();

        List<User> otherUsers = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.id.in(
                        JPAExpressions.select(QBid.bid.user.id)
                                .from(QBid.bid)
                                .where(QBid.bid.amount.eq(new BigDecimal("105.00")))))
                .fetch();

        assertAll(
                () -> assertThat(users).hasSize(2),
                () -> assertThat(otherUsers).hasSize(1),
                () -> assertThat(otherUsers.get(0).getUsername()).isEqualTo("burk")
        );
    }

    @Test
    void testJoin() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .innerJoin(QUser.user.bids, QBid.bid)
                .on(QBid.bid.amount.eq(new BigDecimal("120.00")))
                .fetch();

        List<User> otherUsers = queryFactory.selectFrom(QUser.user)
                .innerJoin(QUser.user.bids, QBid.bid)
                .on(QBid.bid.amount.eq(new BigDecimal("105.00")))
                .fetch();

        assertAll(
                () -> assertThat(users).hasSize(2),
                () -> assertThat(otherUsers).hasSize(1),
                () -> assertThat(otherUsers.get(0).getUsername()).isEqualTo("burk")
        );
    }

    @Test
    void testUpdate() {
        queryFactory.update(QUser.user)
                .where(QUser.user.username.eq("john"))
                .set(QUser.user.email, "john@someotherdoamin.com")
                .execute();

        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();

        String actual = queryFactory.select(QUser.user.email)
                .from(QUser.user)
                .where(QUser.user.username.eq("john"))
                .fetchOne();

        assertThat(actual).isEqualTo("john@someotherdoamin.com");
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void testDelete() {
        User burk = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .fetchOne();

        if (burk != null) {
            queryFactory.delete(QBid.bid)
                    .where(QBid.bid.user.eq(burk))
                    .execute();
        }

        queryFactory.delete(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .execute();

        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();

        assertThat(queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .fetchOne()).isNull();
    }
}
