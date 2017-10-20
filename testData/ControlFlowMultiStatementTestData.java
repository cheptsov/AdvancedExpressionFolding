import java.util.Arrays;

public class ControlFlowMultipleStatementTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) <fold text='' expand='false'>{</fold>
            System.out.println("...");
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        if (args.length == 0) <fold text='' expand='false'>{</fold>
            System.out.println("...");
            System.out.println("...");
        <fold text='' expand='false'>} </fold>else {
            System.out.println("Success");
        }
        if (args.length > 0) {
            System.out.println("Terminating");
        } else <fold text='' expand='false'>{</fold>
            System.out.println("Terminating");
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        for (String arg : args) {
            System.out.println(arg);
        }
        int i = 0;
        for (String arg : args) <fold text='' expand='false'>{</fold>
            System.out.println(i++);
            System.out.println(arg);
<fold text='' expand='false'>        }
</fold>        while (true) <fold text='' expand='false'>{</fold>
            System.out.println("...");
            break;
<fold text='' expand='false'>        }
</fold>        while (true) {
            break;
        }
        try {
            System.out.println("...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try <fold text='' expand='false'>{</fold>
            System.out.println("...");
            System.out.println("...");
        <fold text='' expand='false'>} </fold>catch (Exception e) <fold text='' expand='false'>{</fold>
            System.out.println("...");
            e.printStackTrace();
<fold text='' expand='false'>        }
</fold>        do <fold text='' expand='false'>{</fold>
            System.out.println("...");
            break;
        <fold text='' expand='false'>} </fold>while (true);
        do {
            break;
        } while (true);
    }</fold>
}
