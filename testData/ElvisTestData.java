public class ElvisTestData {
    private ElvisTestData e = create();

    public static void main(String[] args) <fold text='{...}' expand='true'>{
        ElvisTestData e = create();
        System.out.println(<fold text='' expand='false'>e != null ? </fold>e<fold text=' ?: ' expand='false'> : </fold>"");
        System.out.println(<fold text='' expand='false'>e != null ? </fold>e<fold text='?.' expand='false'>.</fold>sayHello()<fold text=' ?: ' expand='false'> : </fold>"");
        System.out.println(e != null && e.get() != null ? e.get() : ""); // Should be System.out.println(e?.get ?: "")
        System.out.println(e != null && e.get() != null ? e.get().sayHello() : ""); // Should be System.out.println(e?.get?.sayHello() ?: "")
        if (e != null) <fold text='{...}' expand='true'>{
                e<fold text='?.' expand='false'>.</fold>get().sayHello();<fold text='' expand='false'>
        }</fold></fold>
        if (e.get() != null) <fold text='{...}' expand='true'>{
                e.get()<fold text='?.' expand='false'>.</fold>sayHello();<fold text='' expand='false'>
        }</fold></fold>
        if (e != null && e.get() != null) <fold text='{...}' expand='true'>{
                e.get().sayHello();
        }</fold>
    }</fold>

    private String sayHello()<fold text=' { ' expand='false'> {
        </fold>return "Hello";<fold text=' }' expand='false'>
    }</fold>

    private static ElvisTestData create() <fold text='{...}' expand='true'>{
        if (Math.random() > 0.5) <fold text='{...}' expand='true'>{
        return new ElvisTestData();
        }</fold> else <fold text='{...}' expand='true'>{
        return null;
        }</fold>
    }</fold>

    private ElvisTestData get()<fold text=' { ' expand='false'> {
        </fold>return e;<fold text=' }' expand='false'>
    }</fold>
}
