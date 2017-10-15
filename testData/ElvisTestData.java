public class ElvisTestData {
    private ElvisTestData e = create();

    public static void main(String[] args) <fold text='{...}' expand='true'>{
        ElvisTestData e = create();
        System.out.println(<fold text='' expand='false'>e != null ? </fold>e<fold text=' ?: ' expand='false'> : </fold>"");
        System.out.println(<fold text='' expand='false'>e != null ? </fold>e<fold text='?.' expand='false'>.</fold>sayHello()<fold text=' ?: ' expand='false'> : </fold>"");
        System.out.println(e != null && e.get() != null ? e.get() : ""); // Should be System.out.println(e?.get ?: "")
        System.out.println(e != null && e.get() != null ? e.get().sayHello() : ""); // Should be System.out.println(e?.get?.sayHello() ?: "")
        <fold text='' expand='false'>if (e != null) {
            </fold>e<fold text='?.' expand='false'>.</fold>get().sayHello();<fold text='' expand='false'>
        }</fold>
        <fold text='' expand='false'>if (e.get() != null) {
            </fold>e.get()<fold text='?.' expand='false'>.</fold>sayHello();<fold text='' expand='false'>
        }</fold>
        if (e != null && e.get() != null) {
            e.get().sayHello();
        }
    }</fold>

    private String sayHello()<fold text=' { ' expand='false'> {
        </fold>return "Hello";<fold text=' }' expand='false'>
    }</fold>

    private static ElvisTestData create() <fold text='{...}' expand='true'>{
        if (Math.random() > 0.5) {
            return new ElvisTestData();
        } else {
            return null;
        }
    }</fold>

    private ElvisTestData get()<fold text=' { ' expand='false'> {
        </fold>return e;<fold text=' }' expand='false'>
    }</fold>
}
