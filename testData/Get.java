import <fold text='...'>java.math.BigDecimal;

import java.util.ArrayList;</fold>

public class Get {
    public static void main(String[] args) <fold text='{...}'>{
        <fold text='var'>int[]</fold> array = <fold text='['>new int[]{</fold>1, 2, 3<fold text=']'>}</fold>;
        System.out.println(array<fold text='.first()'>[0]</fold>);
        System.out.println(array<fold text='.last()'>[array.length - 1]</fold>);

        <fold text='var'>ArrayList<String></fold> list = <fold text='[]'>new ArrayList<fold text='<~>'><String></fold>()</fold>;
        System.out.println(list<fold text='.first'>.get</fold>(<fold text=''>0</fold>));
        System.out.println(list<fold text='['>.get(</fold>5<fold text=']'>)</fold>);
        System.out.println(list<fold text='.last'>.get</fold>(<fold text=''>list.size() - 1</fold>));
    }</fold>
}