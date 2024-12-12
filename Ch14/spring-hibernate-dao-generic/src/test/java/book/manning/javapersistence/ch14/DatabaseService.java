package book.manning.javapersistence.ch14;

import book.manning.javapersistence.ch14.dao.GenericDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
public class DatabaseService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private GenericDao<Item> itemDao;

    public void init() {
        for (int i = 0; i < 10; i++) {
            String name = "Item " + (i + 1);
            Item item = new Item();
            item.setName(name);
            Bid bid1 = new Bid(new BigDecimal("1000.0"), item);
            Bid bid2 = new Bid(new BigDecimal("1100.0"), item);

            itemDao.insert(item);
        }
    }

    public void clear() {
        sessionFactory.getCurrentSession()
                .createMutationQuery("delete from Bid b").executeUpdate();
        sessionFactory.getCurrentSession()
                .createMutationQuery("delete from Item i").executeUpdate();
    }
}
