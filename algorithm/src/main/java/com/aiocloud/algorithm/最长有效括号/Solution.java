package com.aiocloud.algorithm.最长有效括号;

import java.util.Stack;

public class Solution {

    public int longestValidParentheses(String s) {

        if (s.length() < 2) {
            return 0;
        }

        int result = 0;

        Stack<Integer> stack = new Stack<>();
        stack.push(-1);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '(') {
                // start index
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    result = Math.max(result, i - stack.peek());
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int i = solution.longestValidParentheses(")()())");
        System.out.println(i);
        System.out.println(5/2);
    }
}
