package algorithms;

import java.util.Arrays;

/**
 *
 * 时间复杂度为O(N logN),但是由于要多开辟一个数组，一般不用于内排，而用于外排
 *
 */
public class MergeSort {

    public static void main(String[] args) {
        int[] a = {5, 2, 9, 11, 7};

        int[] b = sort(a);
        System.out.println(Arrays.toString(b));
    }


    public static int[] sort(int[] a) {
        int length = a.length;
        int[] b = new int[length]; //开辟一个新数组
        mSort(a, b, 0, length - 1);

        return b;
    }

    public static void mSort(int[] a, int[] b, int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            mSort(a, b, start, mid);
            mSort(a, b, mid + 1, end);
            merge(a, b, start, mid, end);
        }
    }

    /**
     * 将有序的a[start...mid]和有序的a[mid+1...end]归并为有序的b[0...end-start+1],
     * 而后再将b[0...end-start+1]复制到a[start...end]，使a[start...end]有序
     */
    public static void merge(int[] a, int[] b, int start, int mid, int end) {
        int i = start, j = mid + 1, k = 0;

        while (i <= mid && j <= end) {
            if (a[i] <= a[j]) {
                b[k++] = a[i++];
            } else {
                b[k++] = a[j++];
            }
        }

        while (i <= mid) {
            b[k++] = a[i++];
        }

        while (j <= end) {
            b[k++] = a[j++];
        }

        for (i = 0; i < k; i++) {
            a[i + start] = b[i];
        }
    }
}
