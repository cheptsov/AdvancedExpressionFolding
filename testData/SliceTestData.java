import <fold text='...' expand='false'>java.util.Arrays;
import java.util.List;</fold>

public class SliceTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        List<String> list = Arrays.asList(args);
        System.out.println(list<fold text='[' expand='false'>.subList(</fold>1<fold text=':]' expand='false'>, list.size())</fold>);
        System.out.println(list<fold text='[' expand='false'>.subList(</fold>1<fold text=':' expand='false'>, </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(list<fold text='[' expand='false'>.subList(</fold>1<fold text=':]' expand='false'>, list.size())</fold>);
        System.out.println(list<fold text='[' expand='false'>.subList(0</fold><fold text=':' expand='false'>, </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(list<fold text='[' expand='false'>.subList(</fold>1<fold text=':' expand='false'>, list.size() </fold>-<fold text='' expand='false'> </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(list<fold text='[' expand='false'>.subList(0</fold><fold text=':' expand='false'>, list.size() </fold>-<fold text='' expand='false'> </fold>2<fold text=']' expand='false'>)</fold>);
        String f = args[0];
        System.out.println(f<fold text='[' expand='false'>.substring(</fold>1<fold text=':]' expand='false'>)</fold>);
        System.out.println(f<fold text='[' expand='false'>.substring(</fold>1<fold text=':' expand='false'>, </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(f<fold text='[' expand='false'>.substring(</fold>1<fold text=':]' expand='false'>, f.length())</fold>);
        System.out.println(f<fold text='[' expand='false'>.substring(0</fold><fold text=':' expand='false'>, </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(f<fold text='[' expand='false'>.substring(</fold>1<fold text=':' expand='false'>, f.length() </fold>-<fold text='' expand='false'> </fold>2<fold text=']' expand='false'>)</fold>);
        System.out.println(f<fold text='[' expand='false'>.substring(0</fold><fold text=':' expand='false'>, f.length() </fold>-<fold text='' expand='false'> </fold>2<fold text=']' expand='false'>)</fold>);
    }</fold>
}
