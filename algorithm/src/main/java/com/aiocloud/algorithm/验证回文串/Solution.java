package com.aiocloud.algorithm.验证回文串;

public class Solution {

    public boolean isPalindrome(String s) {

        int i = 0;
        int j = s.length() - 1;
        while (i < j) {

            char c1 = s.charAt(i);
            i++;
            if (!Character.isLetterOrDigit(c1)) {
                continue;
            }

            char c2 = s.charAt(j);
            j--;
            if (!Character.isLetterOrDigit(c2)) {
                continue;
            }

            if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.isPalindrome("A man, a plan, a canal: Panama");
    }
}
