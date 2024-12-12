package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import book.manning.javapersistence.ch14.Item;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ItemDaoImpl extends AbstractGenericDao<Item> {

    public ItemDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Item.class);
    }

    @Override
    public void insert(Item item) {
        sessionFactory.getCurrentSession().persist(item);
        for (Bid bid : item.getBids()) {
            sessionFactory.getCurrentSession().persist(bid);
        }
    }

    @Override
    public void delete(Item item) {
        sessionFactory.getCurrentSession()
                .createMutationQuery("delete from Bid b where b.item.id = :id")
                .setParameter("id", item.getId()).executeUpdate();
        sessionFactory.getCurrentSession()
                .createMutationQuery("delete from Item i where i.id = :id")
                .setParameter("id", item.getId()).executeUpdate();
    }
}
