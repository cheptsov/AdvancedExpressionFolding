import <fold text='...'>java.math.BigDecimal;

import java.util.ArrayList;</fold>

public class For {
    public static void main(String[] args) <fold text='{...}'>{
        for <fold text='(('>(</fold><fold text='var'>int</fold> i<fold text=', '> = 0; i < args.length; i++) {
            </fold><fold text='var'>String</fold> arg<fold text=') : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
            System.out.println(i);
        }

        for (<fold text=''><fold text='var'>int</fold> i = 0; i < args.length; i++) {
            </fold><fold text='var'>String</fold> arg<fold text=' : '> = </fold>args<fold text=') {
'>[i];</fold>
            System.out.println(arg);
        }

        for (<fold text='var'>int</fold> i<fold text=' : ['> = </fold>0<fold text=', '>; i < </fold>args.length<fold text=')'>; i++</fold>) {
            System.out.println(i);
        }
    }</fold>
}