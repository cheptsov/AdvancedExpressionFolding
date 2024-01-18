import java.util.Arrays;

public class ControlFlowMultipleStatementTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        System.out.println("...");
        }</fold>
        if (args.length == 0) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        System.out.println("...");
        }</fold> else <fold text='{...}' expand='true'>{
                System.out.println("Success");
        }</fold>
        if (args.length > 0) <fold text='{...}' expand='true'>{
                System.out.println("Terminating");
        }</fold> else <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("Terminating");
        System.out.println("...");
        }</fold>
        for (String arg : args) <fold text='{...}' expand='true'>{
                System.out.println(arg);
        }</fold>
        int i = 0;
        for (String arg : args) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println(i++);
        System.out.println(arg);
        }</fold>
        while (true) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        break;
        }</fold>
        while (true) <fold text='{...}' expand='true'>{
        break;
        }</fold>
        try <fold text='{...}' expand='true'>{
                System.out.println("...");
        }</fold> catch (Exception e) <fold text='{...}' expand='true'>{
                e.printStackTrace();
        }</fold>
        try <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        System.out.println("...");
        }</fold> catch (Exception e) <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        e.printStackTrace();
        }</fold>
        do <fold text='' expand='false'><fold text='{...}' expand='true'>{</fold>
                System.out.println("...");
        break;
        }</fold> while (true);
        do <fold text='{...}' expand='true'>{
        break;
        }</fold> while (true);
    }</fold>
}
