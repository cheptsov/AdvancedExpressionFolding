import java.util.Arrays<fold text='' expand='false'>;</fold>

public class SemicolonTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) <fold text='{...}' expand='true'>{
        for (String arg : args) <fold text='{...}' expand='true'>{
                System.out.println(arg)<fold text='' expand='false'>;</fold>
            }</fold>
        }</fold>
                Arrays.stream(args).forEach(System.out::println)<fold text='' expand='false'>;</fold>
    }</fold>
}
