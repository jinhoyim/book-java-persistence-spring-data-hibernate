package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import book.manning.javapersistence.ch14.Item;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ItemDaoImpl implements ItemDao {

    private final SessionFactory sessionFactory;

    public ItemDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Item getById(long id) {
        return sessionFactory.getCurrentSession().get(Item.class, id);
    }

    @Override
    public List<Item> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Item", Item.class).list();
    }

    @Override
    public void insert(Item item) {
        sessionFactory.getCurrentSession().persist(item);
        for (Bid bid : item.getBids()) {
            sessionFactory.getCurrentSession().persist(bid);
        }
    }

    @Override
    public void update(long id, String name) {
        Item item = sessionFactory.getCurrentSession().get(Item.class, id);
        item.setName(name);
        sessionFactory.getCurrentSession().merge(item);
    }

    @Override
    public void delete(Item item) {
        sessionFactory.getCurrentSession().createQuery("delete from Bid b where b.item.id=:id")
                .setParameter("id", item.getId()).executeUpdate();
        sessionFactory.getCurrentSession().createQuery("delete from Item i where i.id=:id")
                .setParameter("id", item.getId()).executeUpdate();
    }

    @Override
    public Item findByName(String name) {
        return sessionFactory.getCurrentSession().createQuery("from Item where name=:name", Item.class)
                .setParameter("name", name).uniqueResult();
    }
}
