public class StringBuilderAppend {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        StringBuilder sb1 = <fold text='"["' expand='false'>new StringBuilder("[")</fold>;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            sb1<fold text=' += ' expand='false'>.append(</fold>arg<fold text='' expand='false'>)</fold>;
            if (i < args.length - 1) {
                sb1<fold text=' += ' expand='false'>.append(</fold>","<fold text='' expand='false'>)</fold>;
            }
        }
        System.out.println(sb1<fold text=' += ' expand='false'>.append(</fold>"]"<fold text='' expand='false'>).toString()</fold>);

        StringBuilder sb2 = <fold text='' expand='false'>new StringBuilder().append(</fold>"["<fold text='' expand='false'>)</fold>;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            sb2<fold text=' += ' expand='false'>.append(</fold>arg<fold text='' expand='false'>)</fold>;
            if (i < args.length - 1) {
                sb2<fold text=' += ' expand='false'>.append(</fold>","<fold text='' expand='false'>)</fold>;
            }
        }
        System.out.println(sb2<fold text=' += ' expand='false'>.append(</fold>"]"<fold text='' expand='false'>).toString()</fold>);

        StringBuilder sb3 = <fold text='""' expand='false'>new StringBuilder()</fold>;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            sb3<fold text=' += ' expand='false'>.append(</fold>arg<fold text='' expand='false'>)</fold>;
            if (i < args.length - 1) {
                sb3<fold text=' += ' expand='false'>.append(</fold>","<fold text=' + ' expand='false'>).append(</fold>" "<fold text='' expand='false'>)</fold>;
            }
        }
        System.out.println(sb3<fold text='' expand='false'>.toString()</fold>);
    }</fold>
}
