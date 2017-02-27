import java.math.BigDecimal;

public class Elvis {
    public static void main(String[] args) <fold text='{...}'>{
        <fold text='var'>BigDecimal</fold> a = new BigDecimal(1);
        <fold text=''>if (a != null) {
            </fold>a<fold text='?.'>.</fold><fold text='class'>getClass()</fold>.newInstance();<fold text=''>
        }</fold>
        if (a.<fold text='class'>getClass()</fold><fold text='?'> != null</fold>) {
            a.<fold text='class'>getClass()</fold>.newInstance();
        }
        if (a<fold text='?'> != null</fold>) {
            System.out.println(a.<fold text='class'>getClass()</fold>.newInstance());
        }
        System.out.println(<fold text=''>a != null ? </fold>a<fold text=' ?: '> : </fold>"");
        System.out.println(a.<fold text='class'>getClass()<fold text='?'></fold> != null</fold> ? a.<fold text='class'>getClass()</fold> : "");
        System.out.println(a.<fold text='class'>getClass()</fold><fold text='?'> != null</fold> ? a.<fold text='class'>getClass()</fold>.toString() : "");
        System.out.println(a<fold text='?'> != null</fold> ? "a" : "b");

        System.out.println(<fold text=''>a != null ? </fold><fold text='~'>(Object) </fold>copy(a<fold text='?)'>)</fold><fold text=' ?: '> : </fold>"");
    }</fold>

    private static <T> T copy(T object)<fold text=' { '> {
        </fold>return null;<fold text=' }'>
    }</fold>

    public static BigDecimal getA()<fold text=' { '> {
        </fold>return null;<fold text=' }'>
    }</fold>
}