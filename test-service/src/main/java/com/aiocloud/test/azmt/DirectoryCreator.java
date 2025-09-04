package com.aiocloud.test.azmt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class DirectoryCreator {
    public static void main(String[] args) throws IOException {
        // 父目录路径（根据你的需求修改）
        String parentDirectoryPath = "D:\\aiocloud\\AI 自媒体\\202508011";

        // 创建父目录对象
        File parentDirectory = new File(parentDirectoryPath);

        // 如果父目录不存在，则创建
        if (!parentDirectory.exists()) {
            if (parentDirectory.mkdirs()) {
                System.out.println("已创建父目录: " + parentDirectoryPath);
            } else {
                System.out.println("无法创建父目录: " + parentDirectoryPath);
                return;
            }
        }

        // 创建子目录
        for (int i = 1; i < 20; i++) {
            String dirName;
            if(i < 10) {
                dirName = "202508011-0" + i;
            } else {
                dirName = "202508011-" + i;
            }

            File subDir = new File(parentDirectory, dirName);

            if (!subDir.exists()) {
                if (subDir.mkdir()) {
                    System.out.println("已创建目录: " + dirName);
                } else {
                    System.out.println("无法创建目录: " + dirName);
                }
            } else {
                System.out.println("目录已存在，跳过: " + dirName);
            }

            // 在每个目录下创建一个"描述.txt"文件并添加初始内容
            File descriptionFile = new File(subDir, "描述.txt");
            if (!descriptionFile.exists()) {
                try (PrintWriter writer = new PrintWriter(descriptionFile, "UTF-8")) {
                    writer.println("目录描述信息");
                    writer.println("================");
                    System.out.println("已创建并初始化描述文件: " + descriptionFile.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("创建或写入描述文件时出错: " + e.getMessage());
                }
            } else {
                System.out.println("描述文件已存在，跳过: " + descriptionFile.getAbsolutePath());
            }
        }

        System.out.println("目录创建操作完成");
    }
}