import java.util.*;

public class CollectionTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        List<String> list = Arrays.asList(args);
        list.add("one");
        list.remove("one");
        System.out.println(list.add("two"));
        System.out.println(list.remove("two"));
        List<String> singleton = java.util.Collections.singletonList("one");
        list.addAll(singleton);
        list.removeAll(singleton);
        Collections.addAll(list, args);
        System.out.println(singleton);
        List<String> asList = Arrays.asList("one", "two");
        System.out.println(asList);
        List<String> copy = new ArrayList<>(Arrays.asList("one", "two"));
        System.out.println(copy);
        List<String> unmodifiable = Collections.unmodifiableList(Arrays.asList("one", "two"));
        System.out.println(unmodifiable);
        Set<String> set = new HashSet<String>() {
            {
                add("one");
                add("two");
            }
        };
        set.add("three");
        set.remove("three");
        System.out.println(set);
        Set<String> copyOfSet = Collections.unmodifiableSet(new HashSet<String>() {
            {
                add("one");
                add("two");
            }
        });
        System.out.println(copyOfSet);
        String[] strings = new String[] {"one", "two"};
        System.out.println(Arrays.toString(strings));
    }</fold>
}
