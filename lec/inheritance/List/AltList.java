package inheritance;

public class AltList<X, Y> {
    private X item;
    private AltList<Y, X> next;

    AltList(X item, AltList<Y, X> next) {
        this.item = item;
        this.next = next;
    }

    public AltList<Y, X> pairSwapped() {
        X item1 = item;
        Y item2 = next.item;
        if (next.next == null) {
            return new AltList<>(item2, new AltList<>(item1, null));
        } else {
            return new AltList<Y, X>(item2, new AltList<>(item1, next.next.pairSwapped()));
        }
    }

    public static void main(String[] args) {
        AltList<Integer, String> list = new AltList<>(5, new AltList<>("cat", new AltList<>(10, new AltList<>("dog", null))));
        AltList<String, Integer> swappedlist = list.pairSwapped();
        System.out.println(swappedlist.item);
        System.out.println(swappedlist.next.item);
        System.out.println(swappedlist.next.next.item);
        System.out.println(swappedlist.next.next.next.item);

    }
}
