public class CompactControlFlowTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if <fold text='' expand='false'>(</fold>args.length > 0<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                System.out.println("...");
        }</fold>
        for <fold text='' expand='false'>(</fold>String arg : args<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                System.out.println(arg);
        }</fold>
        for <fold text='' expand='false'>(</fold>int i = 0; i < args.length; i++<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                System.out.println(i);
        }</fold>
        while <fold text='' expand='false'>(</fold>true<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                System.out.println("...");
        break;
        }</fold>
        do <fold text='{...}' expand='true'>{
        break;
        }</fold> while <fold text='' expand='false'>(</fold>true<fold text='' expand='false'>)</fold>;
        switch <fold text='' expand='false'>(</fold>args.length<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
        case 0:
            System.out.println("...");
        }</fold>
            try <fold text='{...}' expand='true'>{
                System.out.println("...");
        }</fold> catch <fold text='' expand='false'>(</fold>Exception e<fold text='' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                e.printStackTrace();
        }</fold>
            if (true)<fold text='{...}' expand='true'>{
                System.out.println("...");
        }</fold>
    }</fold>
}
