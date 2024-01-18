import <fold text='...' expand='false'>java.util.ArrayList;
import java.util.List;</fold>

public class ForRangeTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        for <fold text='((' expand='false'>(</fold>int i = 0; i < args.length; i++) <fold text='{...}' expand='true'>{
                String arg<fold text=') : ' expand='false'> = </fold>args<fold text=') {
' expand='false'>[i];</fold>
        System.out.println(arg);
        System.out.println(i);
        }</fold>
        for (int i = 0; i < args.length; i++) <fold text='{...}' expand='true'>{
                String arg<fold text=' : ' expand='false'> = </fold>args<fold text=') {
' expand='false'>[i];</fold>
        System.out.println(arg);
        }</fold>
        for (int i<fold text=' : [' expand='false'> = </fold>0<fold text=', ' expand='false'>; i < </fold>args.length<fold text=')' expand='false'>; i++</fold>) <fold text='{...}' expand='true'>{
                System.out.println(i);
        }</fold>
        for (int i<fold text=' : [' expand='false'> = </fold>0<fold text=', ' expand='false'>; i <= </fold>args.length - 1<fold text=']' expand='false'>; i++</fold>) <fold text='{...}' expand='true'>{
                System.out.println(i);
        }</fold>
                List<String> list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) <fold text='{...}' expand='true'>{
                String a<fold text=' : ' expand='false'> = </fold>list<fold text=') {
' expand='false'>.get(i);</fold>
        System.out.println(a);
        }</fold>
        for <fold text='((' expand='false'>(</fold>int i = 0; i < list.size(); i++) <fold text='{...}' expand='true'>{
                String a<fold text=') : ' expand='false'> = </fold>list<fold text=') {
' expand='false'>.get(i);</fold>
        System.out.println(a);
        System.out.println(i);
        }</fold>
        if (args.length<fold text=' in (' expand='false'> > </fold>0<fold text=', ' expand='false'> && args.length < </fold>2<fold text='))' expand='false'>)</fold> <fold text='{...}' expand='true'>{
                System.out.println(args.length);
        }</fold>
    }</fold>
}