public class GetterSetterTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        GetterSetterTestData d = new GetterSetterTestData();
        d.<fold text='parent = ' expand='false'>setParent(</fold>d<fold text='' expand='false'>)</fold>;
        d.<fold text='name = ' expand='false'>setName(</fold>"Hello"<fold text='' expand='false'>)</fold>;
        d.<fold text='parent' expand='false'>getParent()</fold>.<fold text='name = ' expand='false'>setName(</fold>"Pum!"<fold text='' expand='false'>)</fold>;
        System.out.println(d.<fold text='parent' expand='false'>getParent()</fold>.<fold text='name' expand='false'>getName()</fold>);
    }</fold>

    private GetterSetterTestData parent;
    private String name;

    private void setParent(GetterSetterTestData parent)<fold text=' { ' expand='false'> {
        </fold>this.parent = parent;<fold text=' }' expand='false'>
    }</fold>

    private GetterSetterTestData getParent()<fold text=' { ' expand='false'> {
        </fold>return parent;<fold text=' }' expand='false'>
    }</fold>

    private String getName()<fold text=' { ' expand='false'> {
        </fold>return name;<fold text=' }' expand='false'>
    }</fold>

    private void setName(String name)<fold text=' { ' expand='false'> {
        </fold>this.name = name;<fold text=' }' expand='false'>
    }</fold>
}
