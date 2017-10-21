public class HighlightedTestData {
    public static void main(String[] args) {
        HighlightedTestData t = new HighlightedTestData();
        if (t.getObject() instanceof HighlightedTestData &&
                ((HighlightedTestData) t.getObject()).getObject() instanceof HighlightedTestData) {
            System.out.println(((HighlightedTestData) ((HighlightedTestData) t.getObject()).getObject()).getObject());
            handle(((HighlightedTestData) ((HighlightedTestData) t.getObject()).getObject()));
        }
    }

    private Object getObject() {
        return this;
    }

    private static void handle(HighlightedTestData t) {
        System.out.println(t);
    }
}
