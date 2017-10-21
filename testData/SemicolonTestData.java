import java.util.Arrays<fold text='' expand='false'>;</fold>

public class SemicolonTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) {
            for (String arg : args) {
                System.out.println(arg)<fold text='' expand='false'>;</fold>
            }
        }
        Arrays.stream(args).forEach(System.out::println)<fold text='' expand='false'>;</fold>
    }</fold>
}
