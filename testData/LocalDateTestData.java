import java.time.LocalDate;

class LocalDateTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        LocalDate d1 = LocalDate.of(2018, 12, 10);
        LocalDate d2 = LocalDate.of(2018, 12, 10);
        boolean isBefore = d1<fold text=' < ' expand='false'>.isBefore(</fold>d2<fold text='' expand='false'>)</fold>;
        boolean isAfter = d1<fold text=' > ' expand='false'>.isAfter(</fold>d2<fold text='' expand='false'>)</fold>;
        boolean d2SmallerOrEqualD1 = <fold text='' expand='false'>!</fold>d1<fold text=' ≥ ' expand='false'>.isBefore(</fold>d2<fold text='' expand='false'>)</fold>;;
        boolean d1SmallerOrEqualD2 = <fold text='' expand='false'>!</fold>d1<fold text=' ≤ ' expand='false'>.isAfter(</fold>d2<fold text='' expand='false'>)</fold>;
    }</fold>
}