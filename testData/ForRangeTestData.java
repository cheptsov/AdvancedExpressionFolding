import <fold text='...' expand='false'>java.util.ArrayList;
import java.util.List;</fold>

public class ForRangeTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        for <fold text='((' expand='false'>(</fold>int i<fold text=', ' expand='false'> = 0; i < args.length; i++) {
            </fold>String arg<fold text=') : ' expand='false'> = </fold>args<fold text=') {
' expand='false'>[i];</fold>
            System.out.println(arg);
            System.out.println(i);
        }
        for (<fold text='' expand='false'>int i = 0; i < args.length; i++) {
            </fold>String arg<fold text=' : ' expand='false'> = </fold>args<fold text=') {
' expand='false'>[i];</fold>
            System.out.println(arg);
        }
        for (int i<fold text=' : [' expand='false'> = </fold>0<fold text=', ' expand='false'>; i < </fold>args.length<fold text=')' expand='false'>; i++</fold>) {
            System.out.println(i);
        }
        for (int i<fold text=' : [' expand='false'> = </fold>0<fold text=', ' expand='false'>; i <= </fold>args.length - 1<fold text=']' expand='false'>; i++</fold>) {
            System.out.println(i);
        }
        List<String> list = new ArrayList<>();
        for (<fold text='' expand='false'>int i = 0; i < list.size(); i++) {
            </fold>String a<fold text=' : ' expand='false'> = </fold>list<fold text=') {
' expand='false'>.get(i);</fold>
            System.out.println(a);
        }
        for <fold text='((' expand='false'>(</fold>int i<fold text=', ' expand='false'> = 0; i < list.size(); i++) {
            </fold>String a<fold text=') : ' expand='false'> = </fold>list<fold text=') {
' expand='false'>.get(i);</fold>
            System.out.println(a);
            System.out.println(i);
        }
        if (args.length<fold text=' in (' expand='false'> > </fold>0<fold text=', ' expand='false'> && args.length < </fold>2<fold text='))' expand='false'>)</fold> {
            System.out.println(args.length);
        }
    }</fold>
}