package com.aiocloud.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Test {

    public static void main(String[] args) {

        List<List<String>> lists = groupAnagrams(new String[]{"", "b"});
        System.out.println(JSONObject.toJSONString( lists));

        int[] nums = new int[]{2, 1, 2};
        int i = largestPerimeter(nums);
        // System.out.println(i);
        String[] threeArr = new String[]{"Gold Medal", "Silver Medal", "Bronze Medal"};

        int length = nums.length;

        // Arrays.sort(row, (a, b) -> b - a);
//
//        int[] nums = new int[] {3,1,2,4};
//
//        sortArrayByParity(nums);

//        Arrays.sort(nums);
//        System.out.println(JSONObject.toJSONString(nums));
//        List<Integer> list = new ArrayList<>();
//
//        int[] result = new int[4];

        // 4 5 9
        // 4 4 8 9 9

//        List<Integer> result = new ArrayList<>();
//        Object[] array = result.toArray();
//
//        char r = 0;
//
//        String s = "abcd";
//        String t = "abcde";
//        for (char c1 : s.toCharArray()) {
//            r ^= c1;
//        }
//
//        for (char c1 : t.toCharArray()) {
//            r ^= c1;
//        }
//
//        System.out.println(r);
    }

    public static List<List<String>> groupAnagrams(String[] strs) {

        List<List<String>> result = new ArrayList<>();
        List<String> groupedList = new ArrayList<>();

        for (int i = 0; i < strs.length; i++) {

            String s1 = strs[i];
            if (groupedList.contains(s1)) {
                continue;
            }

            List<String> r1 = new ArrayList<>();
            r1.add(s1);

            for (int j = i + 1; j < strs.length; j++) {
                String s2 = strs[j];
                if (groupedList.contains(s2)) {
                    continue;
                }

                if (checkGroupAnagrams(s1, s2)) {
                    r1.add(s2);
                    groupedList.add(s2);
                }
            }

            result.add(r1);
        }

        return result;
    }

    private static boolean checkGroupAnagrams(String s1, String s2) {

        if (s1.length() != s2.length() || s1.length() == 0) {
            return false;
        }

        int a = 0;
        for (int i = 0; i < s1.length(); i++) {
            a = a ^ s1.charAt(i) ^ s2.charAt(i);
        }

        System.out.println(s1 + "-" + s2 + "-" + a);

        return a == 0;
    }

    public static int[] sortArrayByParity(int[] nums) {

        int len = nums.length;
        int[] result = new int[len];

        int preIndex = 0;
        int lastIndex = len - 1;

        for (int i = 0; i < len; i++) {
            int c = nums[i];
            if (c % 2 == 0) {
                result[preIndex] = c;
                preIndex++;
            } else {
                result[lastIndex] = c;
                preIndex--;
            }
        }

        return result;
    }

    public static int largestPerimeter(int[] nums) {

        int result = 0;

        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {

            int a = nums[i];
            for (int j = i + 1; j < nums.length - 1; j++) {

                int b = nums[j];
                int m1 = Math.max(a, b);
                int m2 = Math.min(a, b);

                for (int k = j + 1; k < nums.length; k++) {

                    int c = nums[k];
                    int m3 = Math.max(m1, c);
                    int m4 = Math.min(m1, c);
                    if (m3 < (m2 + m4)) {
                        int r1 = m3 + m2 + m4;
                        result = Math.max(r1, result);
                    } else {
                        break;
                    }
                }
            }
        }

        return result;
    }
}
