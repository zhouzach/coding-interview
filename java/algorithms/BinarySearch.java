package algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinarySearch {

    public int run(int[] arr, int num) {

        int left = 0;
        int right = arr.length - 1;
        int mid;

        while (left <= right) {

            mid = (left + right) / 2;
            if (arr[mid] > num) {
                right = mid - 1;
            } else if (arr[mid] < num) {
                left = mid + 1;
            } else {
                return mid;
            }
        }

        return -1;
    }


}

class BinarySearchTester {

    private final BinarySearch binarySearch = new BinarySearch();

    @Test
    public void runTester() {

        int[] arr = new int[]{1, 3, 4, 8, 13, 17};
        assertEquals(-1, binarySearch.run(arr, 7));
        assertEquals(3, binarySearch.run(arr, 8));

    }
}
