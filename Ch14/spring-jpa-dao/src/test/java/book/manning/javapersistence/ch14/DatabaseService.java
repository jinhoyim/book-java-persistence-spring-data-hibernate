package book.manning.javapersistence.ch14;

import book.manning.javapersistence.ch14.dao.ItemDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class DatabaseService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private ItemDao itemDao;

    @Transactional
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

    @Transactional
    public void clear() {
        em.createQuery("delete from Bid b").executeUpdate();
        em.createQuery("delete from Item i").executeUpdate();
    }
}