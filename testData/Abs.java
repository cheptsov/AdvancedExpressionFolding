import java.math.BigDecimal;

public class Abs {
    public static void main(String[] args) <fold text='{...}'>{
        <fold text='var'>BigDecimal</fold> a = new BigDecimal(1);
        System.out.println(<fold text='|a|'>a.abs()</fold>);
        System.out.println(<fold text='|a|'>a.abs()</fold>.intValue());
    }</fold>
}