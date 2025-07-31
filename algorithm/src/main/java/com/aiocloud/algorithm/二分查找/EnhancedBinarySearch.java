package com.aiocloud.algorithm.二分查找;

public class EnhancedBinarySearch {

    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int target = 5;

        int i = doBinarySearch(arr, target);
        System.out.println(i);
    }


    public static int doBinarySearch(int[] arr, int target) {

        int left = 0;
        int right = arr.length;

        while (left < right) {

            // 防止在极限值的情况下溢出，例如 (1 + Integer.MAX_VALUE) / 2  溢出了
            int mid = (left + right) >>> 1;

            if (arr[mid] < target) {

                // 右边
                left = mid + 1;
            } else if (arr[mid] > target) {

                // 左边
                right = mid;
            } else if (arr[mid] == target) {

                // 找到准确值
                return mid;
            }
        }

        return -1;
    }
}
