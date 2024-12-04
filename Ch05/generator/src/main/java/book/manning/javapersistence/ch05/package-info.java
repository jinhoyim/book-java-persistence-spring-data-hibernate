@org.hibernate.annotations.GenericGenerator(
        name = "ID_GENERATOR",
//    strategy = "enhanced-sequence",
        type = SequenceStyleGenerator.class,
        parameters = {
                @org.hibernate.annotations.Parameter(
                        name = "sequence_name",
                        value = "MY_SEQUENCE"
                ),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "1000"
                )
        }
)
package book.manning.javapersistence.ch05;

import org.hibernate.id.enhanced.SequenceStyleGenerator;