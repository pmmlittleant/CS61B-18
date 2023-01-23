import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class KthIntList implements Iterator<Integer> {
    public int k;
    private IntList curList;
    private boolean hasNext;

    public KthIntList(IntList I, int k) {
        this.k = k;
        this.curList = I;
        this.hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public Integer next() {
        if (curList == null) {
            throw new NoSuchElementException("No Integers available");
        }
        Integer item = curList.item;
        int x = 0;
        while (x < k) {
            curList = curList.next;
            x += 1;
            if (curList == null) {
                break;
            }
        }
        hasNext = (curList != null);
        return item;
    }
}
