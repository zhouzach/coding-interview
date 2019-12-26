package algorithms;

import java.util.Arrays;

public class HeapSort {

    public static void main(String[] args) {
        int[] a = {5, 2, 9, 11, 7};

        int[] b = heapSort(a);
        System.out.println(Arrays.toString(b));
    }

    public static int[] heapSort(int[] a) {//时间复杂度O(N)
        int len = a.length;
        int heapLen = len;

        buildMaxHeap(a);
        for (int i = len - 1; i >= 1; i--) {
            swap(a, 0, i);
            heapLen--;
            maxHeapify(a, heapLen, 0);
        }

        return a;
    }

    public static void buildMaxHeap(int[] a) { //时间复杂度O(N)
        int len = a.length;

        //i=len/2-1是数组中第一个非叶子节点，
        //而从len/2以后，都是叶子节点，即叶子节点都是平凡最大堆的根节点
        for (int i = len / 2 - 1; i >= 0; i--) {
            maxHeapify(a, len, i);
        }
    }

    //函数执行前的隐含条件是，对于i，其左右子树都满足大顶堆的性质
    public static void maxHeapify(int[] a, int len, int i) { //时间复杂度O(logN)

        int l = 2 * i + 1;
        int r = 2 * i + 2;

        int largest;
        if (l < len && a[l] > a[i]) {
            largest = l;
        } else {
            largest = i;
        }

        if (r < len && a[r] > a[largest]) {
            largest = r;
        }

        if (largest != i) {
            swap(a, i, largest);
            maxHeapify(a, len, largest);
        }

    }
    private static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
