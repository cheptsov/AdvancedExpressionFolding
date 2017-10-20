import java.util.Arrays;

public class ControlFlowSingleStatementTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if (args.length > 0) <fold text='' expand='false'>{</fold>
            System.out.println(Arrays.asList(args));
<fold text='' expand='false'>        }
</fold>        if (args.length == 0) <fold text='' expand='false'>{</fold>
            System.out.println("...");
        <fold text='' expand='false'>} </fold>else <fold text='' expand='false'>{</fold>
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        if (args.length == 0) {
            System.out.println("...");
            System.out.println("...");
        } else <fold text='' expand='false'>{</fold>
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        if (args.length > 0) <fold text='' expand='false'>{</fold>
            System.out.println("...");
        <fold text='' expand='false'>} </fold>else {
            System.out.println("...");
            System.out.println("...");
        }
        for (String arg : args) <fold text='' expand='false'>{</fold>
            System.out.println(arg);
<fold text='' expand='false'>        }
</fold>        for (int i = 0; i < args.length; i++) <fold text='' expand='false'>{</fold>
            System.out.println(args[i]);
<fold text='' expand='false'>        }
</fold>        for (int i = 0; i < args.length; i++) {
            System.out.println(i);
            System.out.println(args[i]);
        }
        while (true) <fold text='' expand='false'>{</fold>
            break;
<fold text='' expand='false'>        }
</fold>        while (true) {
            System.out.println("...");
            break;
        }
        do <fold text='' expand='false'>{</fold>
            break;
        <fold text='' expand='false'>} </fold>while (true);
        do {
            System.out.println("...");
            break;
        } while (true);
        try <fold text='' expand='false'>{</fold>
            System.out.println("...");
        <fold text='' expand='false'>} </fold>catch (Exception e) <fold text='' expand='false'>{</fold>
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        try {
            System.out.println("...");
            System.out.println("...");
        } catch (Exception e) <fold text='' expand='false'>{</fold>
            System.out.println("...");
<fold text='' expand='false'>        }
</fold>        try <fold text='' expand='false'>{</fold>
            System.out.println("...");
        <fold text='' expand='false'>} </fold>catch (Exception e) {
            System.out.println("...");
            System.out.println("...");
        }
    }</fold>
}
