import java.util.Arrays;

public class ControlFlowSingleStatementTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println(Arrays.asList(args));
        }</fold>
        if (args.length == 0) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold> else <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold>
        if (args.length == 0) <fold text='{...}' expand='true'>{
                System.out.println("...");
        System.out.println("...");
        }</fold> else <fold text='{...}' expand='true'><fold text='' expand='false'>{</fold>
                System.out.println("...");
        }</fold>
        if (args.length > 0) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold> else <fold text='{...}' expand='true'>{
                System.out.println("...");
        System.out.println("...");
        }</fold>
        for (String arg : args) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println(arg);
        }</fold>
        for (int i = 0; i < args.length; i++) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println(args[i]);
        }</fold>
        for (int i = 0; i < args.length; i++) <fold text='{...}' expand='true'>{
                System.out.println(i);
        System.out.println(args[i]);
        }</fold>
        while (true) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
        break;
        }</fold>
        while (true) <fold text='{...}' expand='true'>{
                System.out.println("...");
        break;
        }</fold>
        do <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
        break;
        }</fold> while (true);
        do <fold text='{...}' expand='true'>{
                System.out.println("...");
        break;
        }</fold> while (true);
        try <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold> catch (Exception e) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold>
        try <fold text='{...}' expand='true'>{
                System.out.println("...");
        System.out.println("...");
        }</fold> catch (Exception e) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold>
        try <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        }</fold> catch (Exception e) <fold text='{...}' expand='true'>{
                System.out.println("...");
        System.out.println("...");
        }</fold>
    }</fold>
}
