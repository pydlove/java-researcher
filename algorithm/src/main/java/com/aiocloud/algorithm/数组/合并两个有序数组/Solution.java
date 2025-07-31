package com.aiocloud.algorithm.数组.合并两个有序数组;

public class Solution {

    public void merge(int[] nums1, int m, int[] nums2, int n) {

        // 考察点：定点插入
        // 因为有序性，只需要一次遍历

        // 考虑极限情况
        if (n == 0) {
            return;
        }

        int j = 0;
        int size = m;
        if (m > 0) {
            for (int i = 0; i < size && j < n; i++) {

                if (nums1[i] > nums2[j]) {
                    // 左插入
                    System.arraycopy(nums1, i, nums1, i + 1, size - i);
                    size++;
                    nums1[i] = nums2[j];
                    j++;
                }
            }
        }

        for (; j < n; j++) {
            nums1[size] = nums2[j];
            size++;
        }
    }

    public static void main(String[] args) {

        Solution solution = new Solution();
        solution.merge(new int[]{2, 0}, 1, new int[]{1}, 1);
    }
}
