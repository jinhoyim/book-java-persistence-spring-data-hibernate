package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import book.manning.javapersistence.ch14.Item;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ItemDaoImpl extends AbstractGenericDao<Item> {

    public ItemDaoImpl() {
        setClazz(Item.class);
    }

    @Override
    public void insert(Item item) {
        em.persist(item);
        for (Bid bid : item.getBids()) {
            em.persist(bid);
        }
    }

    @Override
    public void delete(Item item) {
        for (Bid bid : item.getBids()) {
            em.remove(bid);
        }
        em.remove(item);
    }
}
