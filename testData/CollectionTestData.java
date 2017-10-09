import java.util.*;

public class CollectionTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        List<String> list = Arrays.asList(args);
        list<fold text=' += ' expand='false'>.add(</fold>"one"<fold text='' expand='false'>)</fold>;
        list<fold text=' -= ' expand='false'>.remove(</fold>"one"<fold text='' expand='false'>)</fold>;
        System.out.println(list<fold text=' + ' expand='false'>.add(</fold>"two"<fold text='' expand='false'>)</fold>);
        System.out.println(list.remove("two"));
        List<String> singleton = <fold text='[' expand='false'>java.util.Collections.singletonList(</fold><fold text='"one"' expand='false'>"one"</fold><fold text=']' expand='false'>)</fold>;
        list<fold text=' += ' expand='false'>.addAll(</fold>singleton<fold text='' expand='false'>)</fold>;
        list<fold text=' -= ' expand='false'>.removeAll(</fold>singleton<fold text='' expand='false'>)</fold>;
        <fold text='' expand='false'>Collections.addAll(</fold>list<fold text=' += ' expand='false'>, </fold>args<fold text='' expand='false'>)</fold>;
        System.out.println(singleton);
        List<String> asList = <fold text='[' expand='false'>Arrays.asList(</fold><fold text='"one"' expand='false'>"one"</fold>, <fold text='"two"' expand='false'>"two"</fold><fold text=']' expand='false'>)</fold>;
        System.out.println(asList);
        List<String> copy = <fold text='[' expand='false'>new ArrayList<>(Arrays.asList(</fold><fold text='"one"' expand='false'>"one"</fold>, <fold text='"two"' expand='false'>"two"</fold><fold text=']' expand='false'>))</fold>;
        System.out.println(copy);
        List<String> unmodifiable = <fold text='[' expand='false'>Collections.unmodifiableList(Arrays.asList(</fold><fold text='"one"' expand='false'>"one"</fold>, <fold text='"two"' expand='false'>"two"</fold><fold text=']' expand='false'>))</fold>;
        System.out.println(unmodifiable);
        Set<String> set = <fold text='[' expand='false'>new HashSet<String>() </fold><fold text='' expand='false'><fold text='{...}' expand='true'>{
            <fold text='' expand='false'><fold text='{...}' expand='true'></fold>{
                add(</fold>"one"<fold text=', ' expand='false'>);
                add(</fold>"two"<fold text='' expand='false'>);
            }</fold></fold><fold text='' expand='false'>
        </fold><fold text=']' expand='false'>}</fold></fold>;
        set<fold text=' += ' expand='false'>.add(</fold>"three"<fold text='' expand='false'>)</fold>;
        set<fold text=' -= ' expand='false'>.remove(</fold>"three"<fold text='' expand='false'>)</fold>;
        System.out.println(set);
        Set<String> copyOfSet = <fold text='[' expand='false'>Collections.unmodifiableSet(new HashSet<String>() </fold><fold text='' expand='false'><fold text='{...}' expand='true'>{
            </fold><fold text='' expand='false'><fold text='{...}' expand='true'>{
                add(</fold>"one"<fold text=', ' expand='false'>);
                add(</fold>"two"<fold text='' expand='false'>);
            }</fold></fold><fold text='' expand='false'>
        </fold><fold text=']' expand='false'>}</fold></fold><fold text='' expand='false'>)</fold>;
        System.out.println(copyOfSet);
        String[] strings = <fold text='[' expand='false'>new String[] {</fold>"one", "two"<fold text=']' expand='false'>}</fold>;
        System.out.println(Arrays.toString(strings));
    }</fold>
}
