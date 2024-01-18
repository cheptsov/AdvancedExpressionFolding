public class TypeCastTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        TypeCastTestData t = new TypeCastTestData();
        if (t.getObject() instanceof TypeCastTestData &&
                <fold text='' expand='false'>((TypeCastTestData) </fold>t.getObject()<fold text='.' expand='false'>).</fold>getObject() instanceof TypeCastTestData) <fold text='{...}' expand='true'>{
                System.out.println(<fold text='' expand='false'>((TypeCastTestData) </fold><fold text='' expand='false'>((TypeCastTestData) </fold>t.getObject()<fold text='.' expand='false'>).</fold>getObject()<fold text='.' expand='false'>).</fold>getObject());
        handle(<fold text='' expand='false'>((TypeCastTestData) </fold><fold text='' expand='false'>((TypeCastTestData) </fold>t.getObject()<fold text='.' expand='false'>).</fold>getObject()<fold text='' expand='false'>)</fold>);
        }</fold>
    }</fold>

    private Object getObject()<fold text=' { ' expand='false'> {
        </fold>return this;<fold text=' }' expand='false'>
    }</fold>

    private static void handle(TypeCastTestData t)<fold text=' { ' expand='false'> {
        </fold>System.out.println(t);<fold text=' }' expand='false'>
    }</fold>
}
