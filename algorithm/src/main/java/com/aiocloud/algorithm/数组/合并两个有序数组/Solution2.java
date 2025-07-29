package com.aiocloud.algorithm.数组.合并两个有序数组;

import java.util.Arrays;

public class Solution2 {

    public void merge(int[] nums1, int m, int[] nums2, int n) {

        // 考察点：定点插入
        // 因为有序性，只需要一次遍历

        // 使用同向双指针法，从尾部方向开始
        // 因为从小到大，可以从 nums1 的尾部开始插入

        // 极限情况
        if (n == 0) {
            return;
        }

        if (m == 0) {
            System.arraycopy(nums2, 0, nums1, 0, n);
            return;
        }

        int m1 = m - 1;
        int n1 = n - 1;
        int newIndex = m + n - 1;

        while (m1 >= 0 && n1 >= 0) {

            // nums1[newIndex] = ?
            // 应该是 m1 和 n1 处大的值
            if (nums1[m1] > nums2[n1]) {
                nums1[newIndex] = nums1[m1];
                m1--;
            } else {
                nums1[newIndex] = nums2[n1];
                n1--;
            }

            newIndex--;
        }
    }

    public static void main(String[] args) {

        Solution2 solution = new Solution2();
        int[] nums1 = {2, 0};
        solution.merge(nums1, 1, new int[]{1}, 1);

        System.out.println(Arrays.toString(nums1));
    }
}
