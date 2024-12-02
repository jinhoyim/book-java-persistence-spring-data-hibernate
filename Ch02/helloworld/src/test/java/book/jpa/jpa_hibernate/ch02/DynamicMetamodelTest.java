package book.jpa.jpa_hibernate.ch02;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.metamodel.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicMetamodelTest {
    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.password", System.getenv("LOCAL_DEVDB_SUPER_PASSWORD"));

        return Persistence.createEntityManagerFactory("ch02", properties);
    }

    @Test
    void metamodel_test() {
        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            Metamodel metamodel = emf.getMetamodel();

            Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
            ManagedType<?> type = managedTypes.iterator().next();

            assertAll(
                    () -> assertEquals(2, managedTypes.size()),
                    () -> assertEquals(Type.PersistenceType.ENTITY,
                            type.getPersistenceType())
            );
        }
    }

    @Test
    void metamodel_test2() {
        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            Metamodel metamodel = emf.getMetamodel();

            Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
            ManagedType<?> messageType = managedTypes.stream()
                    .filter(t -> t.getJavaType().equals(Message.class))
                    .findFirst()
                    .get();

            SingularAttribute<?, ?> textAttribute = messageType.getSingularAttribute("text");

            assertAll(
                    () -> assertTrue(textAttribute.isOptional()),
                    () -> assertEquals(String.class, textAttribute.getJavaType()),
                    () -> assertEquals(Attribute.PersistentAttributeType.BASIC,
                            textAttribute.getPersistentAttributeType())
            );
        }
    }

    @Test
    void metamodel_test3() {
        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            Metamodel metamodel = emf.getMetamodel();

            Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
            ManagedType<?> itemType = managedTypes.stream()
                    .filter(t -> t.getJavaType().equals(Item.class))
                    .findFirst()
                    .get();

            SingularAttribute<?, Long> id = itemType.getSingularAttribute("id", Long.class);
            SingularAttribute<?, String> name = itemType.getSingularAttribute("name", String.class);
            SingularAttribute<?, LocalDate> auctionEnd = itemType.getSingularAttribute("auctionEnd", LocalDate.class);

            assertAll(
                    () -> assertFalse(id.isOptional()),
                    () -> assertEquals(Attribute.PersistentAttributeType.BASIC,
                            name.getPersistentAttributeType()),
                    () -> assertFalse(auctionEnd.isCollection()),
                    () -> assertFalse(auctionEnd.isAssociation())
            );
        }
    }
}
