public class AppendSetterInterpolatedStringTestData {
    private String name;

    public static void main(String[] args) <fold text='{...}' expand='true'>{
        StringBuilder sb1 = <fold text='' expand='false'>new StringBuilder().append(</fold>args[0]<fold text='' expand='false'>)</fold>;
        sb1<fold text=' += ' expand='false'>.append(</fold>"Hello, <fold text='${' expand='false'>" + </fold>args[0]<fold text='}"' expand='false'>)</fold>;
        System.out.println(sb1<fold text='' expand='false'>.toString()</fold>);
        StringBuilder sb2 = <fold text='""' expand='false'>new StringBuilder("")</fold>;
        sb2<fold text=' += ' expand='false'>.append</fold><fold text='"${' expand='false'>(</fold>args[0]<fold text='}' expand='false'> + "</fold>, hello!"<fold text='' expand='false'>)</fold>;
        System.out.println(sb2<fold text='' expand='false'>.toString()</fold>);
        StringBuilder sb3 = <fold text='"Hello, "' expand='false'>new StringBuilder("Hello, ")</fold><fold text=' + ' expand='false'>.append(</fold>args[0]<fold text='' expand='false'>)</fold>; // Should be StringBuilder sb3 = "Hello, $(args[0)":
        System.out.println(sb3);

        new AppendSetterInterpolatedStringTestData().<fold text='name = ' expand='false'>setName(</fold>"Hello, <fold text='${' expand='false'>" + </fold>args[0]<fold text='}"' expand='false'>)</fold>;
        new AppendSetterInterpolatedStringTestData().<fold text='name = ' expand='false'>setName</fold><fold text='"${' expand='false'>(</fold>args[0]<fold text='}' expand='false'> + "</fold>, hello!"<fold text='' expand='false'>)</fold>;
    }</fold>

    public void setName(String name)<fold text=' { ' expand='false'> {
        </fold>this.name = name;<fold text=' }' expand='false'>
    }</fold>
}
