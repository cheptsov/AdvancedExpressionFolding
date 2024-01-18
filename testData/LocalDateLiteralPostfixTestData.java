import java.time.LocalDate;

class LocalDateLiteralTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        LocalDate d1 = <fold text='' expand='false'>LocalDate.of(</fold>2018<fold text='Y-' expand='false'>, </fold>01<fold text='M-' expand='false'>, </fold>10<fold text='D' expand='false'>)</fold>;
        LocalDate d1 = <fold text='' expand='false'>LocalDate.of(</fold>2018<fold text='Y-' expand='false'>, </fold>01<fold text='M-' expand='false'>, </fold>10<fold text='D' expand='false'>)</fold>;
        LocalDate d2 = <fold text='' expand='false'>LocalDate.of(</fold>2018<fold text='Y-' expand='false'>, </fold>12<fold text='M-' expand='false'>, </fold>10<fold text='D' expand='false'>)</fold>;
        LocalDate d3 = <fold text='' expand='false'>LocalDate.of(</fold>2018<fold text='Y-0' expand='false'>,  </fold>4<fold text='M-0' expand='false'> ,  </fold>4<fold text='D' expand='false'>   )</fold>;
        boolean isBefore = d1.isBefore(d2);
        boolean isAfter = d1.isAfter(d2);
        boolean d2SmallerOrEqualD1 = !d1.isBefore(d2);
        boolean d1SmallerOrEqualD2 = !d1.isAfter(<fold text='' expand='false'>LocalDate.of(</fold>2013<fold text='Y-0' expand='false'>, </fold>1<fold text='M-' expand='false'>, </fold>10<fold text='D' expand='false'>)</fold>);
    }</fold>
}