package book.manning.javapersistence.ch14.dao;

import book.manning.javapersistence.ch14.Bid;
import book.manning.javapersistence.ch14.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ItemDaoImpl implements ItemDao {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    public Item getById(long id) {
        return em.find(Item.class, id);
    }

    @Override
    public List<Item> getAll() {
        return em.createQuery("from Item", Item.class).getResultList();
    }

    @Override
    public void insert(Item item) {
        em.persist(item);
        for (Bid bid : item.getBids()) {
            em.persist(bid);
        }
    }

    @Override
    public void update(long id, String name) {
        Item item = em.find(Item.class, id);
        item.setName(name);
        em.persist(item);
    }

    @Override
    public void delete(Item item) {
        for (Bid bid : item.getBids()) {
            em.remove(bid);
        }
        em.remove(item);
    }

    @Override
    public Item findByName(String name) {
        return em.createQuery("from Item where name=:name", Item.class)
                .setParameter("name", name).getSingleResult();
    }
}
