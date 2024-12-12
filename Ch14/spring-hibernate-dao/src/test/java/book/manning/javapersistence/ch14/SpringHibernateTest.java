package book.manning.javapersistence.ch14;

import book.manning.javapersistence.ch14.configuration.SpringConfiguration;
import book.manning.javapersistence.ch14.dao.BidDao;
import book.manning.javapersistence.ch14.dao.ItemDao;
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
class SpringHibernateTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BidDao bidDao;

    @BeforeEach
    public void setUp() {
        databaseService.init();
    }

    @AfterEach
    public void tearDown() {
        databaseService.clear();
    }

    @Test
    void testInsertItems() {
        List<Item> items = itemDao.getAll();
        List<Bid> bids = bidDao.getAll();
        assertAll(
                () -> assertThat(items).isNotNull(),
                () -> assertThat(items).hasSize(10),
                () -> assertThat(itemDao.findByName("Item 1")).isNotNull(),
                () -> assertThat(bids).isNotNull(),
                () -> assertThat(bids).hasSize(20),
                () -> assertThat(bidDao.findByAmount("1000.00")).hasSize(10)
        );
    }

    @Test
    void testDeleteItem() {
        itemDao.delete(itemDao.findByName("Item 2"));
        Item actual = itemDao.findByName("Item 2");
        assertThat(actual).isNull();
    }

    @Test
    void testUpdateItem() {
        Item item1 = itemDao.findByName("Item 1");
        itemDao.update(item1.getId(), "Item 1_updated");
        assertThat(itemDao.getById(item1.getId()).getName()).isEqualTo("Item 1_updated");
    }

    @Test
    void testInsertBid() {
        Item item3 = itemDao.findByName("Item 3");
        Bid newBid = new Bid(new BigDecimal("2000.00"), item3);
        bidDao.insert(newBid);
        assertAll(
                () -> assertThat(bidDao.getById(newBid.getId()).getAmount()).isEqualTo(new BigDecimal("2000.00")),
                () -> assertThat(bidDao.getAll()).hasSize(21)
        );
    }

    @Test
    void testUpdateBid() {
        Bid bid = bidDao.findByAmount("1000.00").get(0);
        bidDao.update(bid.getId(), "1200.00");
        assertThat(bidDao.getById(bid.getId()).getAmount()).isEqualTo(new BigDecimal("1200.00"));
    }

    @Test
    void testDeleteBid() {
        bidDao.delete(bidDao.findByAmount("1000.00").get(0));
        assertThat(bidDao.getAll()).hasSize(19);
    }
}