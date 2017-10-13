public class EqualsCompareTestData implements Comparable<EqualsCompareTestData> {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        EqualsCompareTestData a = new EqualsCompareTestData();
        EqualsCompareTestData b = new EqualsCompareTestData();
        System.out.println(a<fold text=' ≡ ' expand='false'>.equals(</fold>b<fold text='' expand='false'>)</fold>);
        System.out.println(<fold text='' expand='false'>!</fold>a<fold text=' ≢ ' expand='false'>.equals(</fold>b<fold text='' expand='false'>)</fold>);
        System.out.println(a<fold text=' ≡ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) == 0</fold>);
        System.out.println(a<fold text=' ≢ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) != 0</fold>);

        System.out.println(a<fold text=' > ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) > 0</fold>);
        System.out.println(a<fold text=' > ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) == 1</fold>);
        System.out.println(a<fold text=' ≥ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) > -1</fold>);
        System.out.println(a<fold text=' ≥ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) >= 0</fold>); // Should be a >= b

        System.out.println(a<fold text=' < ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) < 0</fold>);
        System.out.println(a<fold text=' < ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) == -1</fold>);
        System.out.println(a<fold text=' ≤ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) < 1</fold>);
        System.out.println(a<fold text=' ≤ ' expand='false'>.compareTo(</fold>b<fold text='' expand='false'>) <= 0</fold>); // Should be a <= b
    }</fold>

    @Override
    public int compareTo(EqualsCompareTestData o)<fold text=' { ' expand='false'> {
        </fold>return 0;<fold text=' }' expand='false'>
    }</fold>
}
