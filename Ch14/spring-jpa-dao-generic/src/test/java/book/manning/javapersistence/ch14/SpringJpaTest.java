package book.manning.javapersistence.ch14;

import book.manning.javapersistence.ch14.configuration.SpringConfiguration;
import book.manning.javapersistence.ch14.dao.GenericDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {SpringConfiguration.class})
@ContextConfiguration("classpath:application-context.xml")
class SpringJpaTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private GenericDao<Item> itemDao;

    @Autowired
    private GenericDao<Bid> bidDao;

    @BeforeEach
    void setUp() {
        databaseService.init();
    }

    @AfterEach
    void tearDown() {
        databaseService.clear();
    }

    @Test
    void testInsertItems() {
        List<Item> items = itemDao.getAll();
        List<Bid> bids = bidDao.getAll();
        assertAll(
                () -> assertThat(items).isNotNull(),
                () -> assertThat(items).hasSize(10),
                () -> assertThat(itemDao.findByProperty("name", "Item 1")).isNotNull(),
                () -> assertThat(bids).isNotNull(),
                () -> assertThat(bids).hasSize(20),
                () -> assertThat(bidDao.findByProperty("amount", "1000.00")).hasSize(10)
        );
    }

    @Test
    void testDeleteItem() {
        itemDao.delete((itemDao.findByProperty("name", "Item 2").get(0)));
        assertThat(itemDao.findByProperty("name", "Item 2")).isEmpty();
    }

    @Test
    void testUpdateItem() {
        List<Item> items = itemDao.findByProperty("name", "Item 1");
        itemDao.update(items.get(0).getId(), "name", "Item 1_updated");
        assertThat(itemDao.findByProperty("name", "Item 1_updated")).hasSize(1);
    }

    @Test
    void testInsertBid() {
        Item item3 = itemDao.findByProperty("name", "Item 3").get(0);
        Bid newBid = new Bid(new BigDecimal("2000.00"), item3);
        bidDao.insert(newBid);
        assertAll(
                () -> assertThat(bidDao.getById(newBid.getId()).getAmount())
                        .isEqualTo(new BigDecimal("2000.00")),
                () -> assertThat(bidDao.getAll()).hasSize(21)
        );
    }

    @Test
    void testUpdateBid() {
        Bid bid = bidDao.findByProperty("amount", new BigDecimal("1000.00")).get(0);
        bidDao.update(bid.getId(), "amount", new BigDecimal("1200.00"));
        assertThat(bidDao.findByProperty("amount", new BigDecimal("1200.00"))).hasSize(1);
    }

    @Test
    void testDeleteBid() {
        bidDao.delete(bidDao.findByProperty("amount", new BigDecimal("1000.00")).get(0));
        assertThat(bidDao.getAll()).hasSize(19);
    }
}