package com.aiocloud.test.azmt;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class PngRenamer {
    public static void main(String[] args) {
        // 指定目录路径（根据你的图片路径修改）
        String directoryPath = "D:\\aiocloud\\AI 自媒体\\20250806-06";

        File directory = new File(directoryPath);

        // 获取目录下所有PNG文件
        File[] pngFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (pngFiles == null || pngFiles.length == 0) {
            System.out.println("目录中没有PNG图片文件");
            return;
        }

        // 按修改时间排序（如果你想按名称排序可以修改这个比较器）
        Arrays.sort(pngFiles, Comparator.comparingLong(File::lastModified));

        // 重命名文件
        int count = 1;
        for (File pngFile : pngFiles) {
            String newName = count + ".png";
            File newFile = new File(directoryPath, newName);

            // 如果目标文件已存在，先删除
            if (newFile.exists()) {
                newFile.delete();
            }

            if (pngFile.renameTo(newFile)) {
                System.out.println("重命名成功: " + pngFile.getName() + " -> " + newName);
                count++;
            } else {
                System.out.println("重命名失败: " + pngFile.getName());
            }
        }

        System.out.println("共处理了 " + (count - 1) + " 个PNG文件");
    }
}