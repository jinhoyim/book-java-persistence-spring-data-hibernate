package book.manning.javapersistence.ch12.fetchloadgraph;

class TestData {
    final Long[] identifiers;

    TestData(Long[] identifiers) {
        this.identifiers = identifiers;
    }

    Long getFirstId() {
        return identifiers.length > 0 ? identifiers[0] : null;
    }

    Long getLastId() {
        return identifiers.length > 0 ? identifiers[identifiers.length - 1] : null;
    }
}