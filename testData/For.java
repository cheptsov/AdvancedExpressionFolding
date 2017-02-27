import <fold text='...'>java.math.BigDecimal;

import java.util.ArrayList;</fold>

public class For {
    public static void main(String[] args) <fold text='{...}'>{
        for <fold text='(var ('>(<fold text='var'>int</fold> </fold>i<fold text=','> = 0; i < args.length; i++) {
            <fold text='var'>String</fold></fold> arg<fold text=') : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
            System.out.println(i);
        }

        for (<fold text='var'><fold text=''>int</fold> i = 0; i < args.length; i++) {
            </fold><fold text='var'>String</fold> arg<fold text=' : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
        }

        for (<fold text='var'>int</fold> i<fold text=' : ['> = </fold>0<fold text=', '>; i < </fold>args.length<fold text=')'>; i++</fold>) {
            System.out.println(i);
        }

        <fold text='var'>List<String></fold> list = <fold text='[]'>new ArrayList<>()</fold>;

        for (<fold text='var'><fold text=''>int</fold> i = 0; i < list.size(); i++) {
            </fold><fold text='var'>String</fold> a<fold text=' : '> = </fold>list<fold text=') {
'>.get(i);</fold>
            System.out.println(a);
        }

        for <fold text='(var ('>(<fold text='var'>int</fold> </fold>i<fold text=','> = 0; i < list.size(); i++) {
            <fold text='var'>String</fold></fold> a<fold text=') : '> = </fold>list<fold text=') {
'>.get(i);</fold>
            System.out.println(a);
            System.out.println(i);
        }

        for <fold text='(val ('>(<fold text='var'>int</fold> </fold>i<fold text=','> = 0; i < args.length; i++) {
            <fold text='val'>final String</fold></fold> arg<fold text=') : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
            System.out.println(i);
        }

        for (<fold text='var'><fold text=''>int</fold> i = 0; i < args.length; i++) {
            </fold><fold text='val'>final String</fold> arg<fold text=' : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
        }

        for (<fold text='var'>int</fold> i<fold text=' : ['> = </fold>0<fold text=', '>; i < </fold>args.length<fold text=')'>; i++</fold>) {
            System.out.println(i);
        }

        <fold text='var'>List<String></fold> list = <fold text='[]'>new ArrayList<>()</fold>;

        for (<fold text='var'><fold text=''>int</fold> i = 0; i < list.size(); i++) {
            </fold><fold text='val'>final String</fold> a<fold text=' : '> = </fold>list<fold text=') {
'>.get(i);</fold>
            System.out.println(a);
        }

        for <fold text='(val ('>(<fold text='var'>int</fold> </fold>i<fold text=','> = 0; i < list.size(); i++) {
            <fold text='val'>final String</fold></fold> a<fold text=') : '> = </fold>list<fold text=') {
'>.get(i);</fold>
            System.out.println(a);
            System.out.println(i);
        }
    }</fold>
}