import <fold text='...' expand='false'>java.util.*;
import java.util.stream.Collectors;</fold>

public class ConcatenationTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        List<String> list = Arrays.asList(args);
        list<fold text=' += ' expand='false'>.add(</fold>"one"<fold text='' expand='false'>)</fold>;
        list<fold text=' -= ' expand='false'>.remove(</fold>"one"<fold text='' expand='false'>)</fold>;
        System.out.println(list.add("two"));
        System.out.println(list.remove("two"));
        List<String> singleton = Collections.emptyList();
        list<fold text=' += ' expand='false'>.addAll(</fold>singleton<fold text='' expand='false'>)</fold>;
        list<fold text=' -= ' expand='false'>.removeAll(</fold>singleton<fold text='' expand='false'>)</fold>;
        <fold text='' expand='false'>Collections.addAll(</fold>list<fold text=' += ' expand='false'>, </fold>args<fold text='' expand='false'>)</fold>;
        Set<String> set = new HashSet<>();
        set<fold text=' += ' expand='false'>.add(</fold>"three"<fold text='' expand='false'>)</fold>;
        set<fold text=' -= ' expand='false'>.remove(</fold>"three"<fold text='' expand='false'>)</fold>;
        System.out.println(set);
        Set<String> copyOfSet = new HashSet<>();
        set<fold text=' += ' expand='false'>.addAll(</fold>copyOfSet<fold text='' expand='false'>)</fold>;
        System.out.println(copyOfSet);
        List<String> streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = <fold text='' expand='false'>Arrays.stream(</fold>args<fold text='' expand='false'>)</fold>.map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);

        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);
        streamToList = list<fold text='.' expand='false'>.stream().</fold>map(String::toUpperCase)<fold text='.' expand='false'>.collect(Collectors.</fold>toList()<fold text='' expand='false'>)</fold>;
        System.out.println(streamToList);

        long count = streamToList<fold text='.' expand='false'>.stream().</fold>distinct().count();
        System.out.println(count);
    }</fold>
}
