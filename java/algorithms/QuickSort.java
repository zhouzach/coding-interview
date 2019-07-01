package algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuickSort {

    public void qsort(int[] arr, int left, int right) {

        if (right <= left) return;

        int i = partition(arr, left, right);

        qsort(arr, left, i - 1);
        qsort(arr, i + 1, right);
    }

    private int partition(int[] arr, int left, int right) {

        int i = left - 1;
        int j = right;

        for (; ; ) {
            while (arr[++i] < arr[right]) {
            }

            while (arr[--j] > arr[right]) {
                if (j == left) {
                    break;
                }
            }

            if (i >= j) {
                break;
            }

            swap(arr, i, j);
        }
        swap(arr, i, right);

        return i;
    }

    private void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}

class QuickSortTester {
    private final QuickSort quickSort = new QuickSort();

    @Test
    public void testQsort() {
        int[] arr = new int[]{5, 3, 7, 1, 2};

        quickSort.qsort(arr, 0, 4);
        assertEquals(1, arr[0]);
        assertEquals(2, arr[1]);
        assertEquals(3, arr[2]);
        assertEquals(5, arr[3]);
        assertEquals(7, arr[4]);

    }
}