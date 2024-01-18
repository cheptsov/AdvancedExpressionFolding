public class VarTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        <fold text='val' expand='false'>String</fold> string = "Hello, world";
        System.out.println();
        <fold text='var' expand='false'>int</fold> count = 0;
        for (<fold text='val' expand='false'>String</fold> arg : args) <fold text='{...}' expand='true'>{
                System.out.println(arg);
        count++;
        }</fold>
        for (<fold text='var' expand='false'>int</fold> i = 0; i < args.length; i++) <fold text='{...}' expand='true'>{
                <fold text='val' expand='false'>String</fold> arg = args[i];
        System.out.println(arg);
        i++;
        }</fold>
    }</fold>
}
