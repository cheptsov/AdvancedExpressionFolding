public class AssertTestData {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        if (args.length == 1) {
            throw new IllegalArgumentException("...");
        }
        if (args.length == 2)
            throw new IllegalArgumentException("...");
    }
}
