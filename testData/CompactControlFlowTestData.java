public class CompactControlFlowTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        if <fold text='' expand='false'>(</fold>args.length > 0<fold text='' expand='false'>)</fold> {
            System.out.println("...");
        }
        for <fold text='' expand='false'>(</fold>String arg : args<fold text='' expand='false'>)</fold> {
            System.out.println(arg);
        }
        for <fold text='' expand='false'>(</fold>int i = 0; i < args.length; i++<fold text='' expand='false'>)</fold> {
            System.out.println(i);
        }
        while <fold text='' expand='false'>(</fold>true<fold text='' expand='false'>)</fold> {
            System.out.println("...");
            break;
        }
        do {
            break;
        } while <fold text='' expand='false'>(</fold>true<fold text='' expand='false'>)</fold>;
        switch <fold text='' expand='false'>(</fold>args.length<fold text='' expand='false'>)</fold> {
            case 0:
                System.out.println("...");
        }
        try {
            System.out.println("...");
        } catch <fold text='' expand='false'>(</fold>Exception e<fold text='' expand='false'>)</fold> {
            e.printStackTrace();
        }
    }</fold>
}
