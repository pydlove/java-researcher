package com.aiocloud.algorithm.二分查找;

import java.util.LinkedList;
import java.util.Queue;

public class BaseBinarySearch {


    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int target = 5;

        int i = doBinarySearch(arr, target);
        System.out.println(i);
    }


    public static int doBinarySearch(int[] arr, int target) {

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (arr[mid] < target) {

                // 右边
                left = mid + 1;
            } else if (arr[mid] > target) {

                // 左边
                right = mid - 1;
            } else if (arr[mid] == target) {

                // 找到准确值
                return mid;
            }
        }

        return -1;
    }
}
