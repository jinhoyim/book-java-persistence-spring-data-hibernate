package book.manning.javapersistence.ch14;

import book.manning.javapersistence.ch14.dao.ItemDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
public class DatabaseService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ItemDao itemDao;

    public void init() {
        for (int i = 0; i < 10; i++) {
            String name = "Item " + (i + 1);
            Item item = new Item();
            item.setName(name);
            Bid bid1 = new Bid(new BigDecimal("1000.00"), item);
            Bid bid2 = new Bid(new BigDecimal("1100.00"), item);

            itemDao.insert(item);
        }
    }

    public void clear() {
        sessionFactory.getCurrentSession().createQuery("delete from Bid b").executeUpdate();
        sessionFactory.getCurrentSession().createQuery("delete from Item i").executeUpdate();
    }
}
