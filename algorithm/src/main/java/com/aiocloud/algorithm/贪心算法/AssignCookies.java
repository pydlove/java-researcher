package com.aiocloud.algorithm.贪心算法;

import java.util.Arrays;

/**
 *
 * @description: AssignCookies.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-05-26 16:46 
 */
public class AssignCookies {

    // 题目
    // 假设你是一位家长，要给孩子们分发一些饼干。每个孩子 i 有一个胃口值 g[i]，每个饼干 j 有一个尺寸 s[j]。
    // 只有当 s[j] >= g[i] 时，才能将饼干 j 分配给孩子 i。
    // 目标是尽可能满足更多的孩子，并返回最多能满足的孩子数量。
    //
    // 输入: g = [1,2,3], s = [1,1]
    // 输出: 1
    // 解释: 只能满足胃口为1的孩子（用1尺寸的饼干），无法满足胃口更大的孩子。
    //
    // 解题思路：
    // 学生胃口和饼干都先排序
    // 双指针遍历，小饼干满足小胃口的孩子

    public static void main(String[] args) {

        int[] children = {1, 2, 3};
        int[] cookies = {1, 1};

        int child = assignCookies(children, cookies);
        System.out.println(child);
    }

    private static int assignCookies(int[] children, int[] cookies) {

        // 升序
        Arrays.sort(children);
        Arrays.sort(cookies);

        int child = 0;
        int cookie = 0;
        while (child < children.length && cookie < cookies.length) {

            if (children[child] <= cookies[cookie]) {
                child++;
            }

            cookie++;
        }

        return child;
    }
}
