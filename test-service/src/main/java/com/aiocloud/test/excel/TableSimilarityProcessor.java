package com.aiocloud.test.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 按表统计相似度 已废弃
 * @description: TableSimilarityProcessor.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-22 14:30 
 */
@Deprecated
@Slf4j
public class TableSimilarityProcessor {

    public static void main(String[] args) throws Exception {

        // 1. 读取Excel文件
        String excelFileName = "test.xlsx";
        String basePath = new File("").getAbsolutePath() + File.separator + "test-service" +
                File.separator + "src" + File.separator + "main" +
                File.separator + "java" + File.separator +
                "com" + File.separator + "aiocloud" + File.separator +
                "test" + File.separator + "excel" + File.separator;
        String inputFilePath = basePath +
                excelFileName;

        FileInputStream fis = new FileInputStream(inputFilePath);
        Workbook workbook = WorkbookFactory.create(fis);

        // 获取第二个sheet（字段核验信息）
        Sheet sheet = workbook.getSheetAt(1);

        // 2. 解析数据
        List<TableRowData> allRows = parseAllRowData(sheet);
        log.info("Total rows: " + allRows.size());

        List<TableInfo> tables = groupRowsIntoTableInfo(allRows);
        log.info("Total tables: " + tables.size());

        // 3. 计算表相似度并分组
        List<List<TableInfo>> tableGroups = groupSimilarTables(tables);
        log.info("Complete similarity calculation");

        // 4. 按组大小排序（从大到小）
        tableGroups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));

        // 5. 选择字段数据直到达到1000个左右
        List<TableRowData> selectedRows = selectRows(allRows, tableGroups, 1000);
        log.info("Complete selected rows: {}", selectedRows.size());

        // 6. 将选中的行写入新的Excel文件，保留所有原始列
        String outputPath = basePath + File.separator + "selected_fields_full_columns.xlsx";
        writeSelectedRowsToExcel(selectedRows, sheet, outputPath);
        log.info("Selected fields written to: " + "selected_fields_full_columns.xlsx");

        workbook.close();
        fis.close();
    }

    // 解析所有行数据（保留所有列信息）
    private static List<TableRowData> parseAllRowData(Sheet sheet) {

        List<TableRowData> rows = new ArrayList<>();

        // 假设第一行是表头，从第二行开始读取数据
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            TableRowData rowData = new TableRowData();
            // 读取所有列数据（根据实际列数调整）
            for (int col = 0; col < 13; col++) { // 假设有13列
                rowData.addColumnValue(getCellStringValue(row.getCell(col)));
            }
            rows.add(rowData);
        }

        return rows;
    }

    // 将行数据分组到表信息中
    private static List<TableInfo> groupRowsIntoTableInfo(List<TableRowData> allRows) {

        Map<String, TableInfo> tableMap = new HashMap<>();

        int i = 0;
        for (TableRowData row : allRows) {

            i++;
            String tableId = row.getColumnValue(3); // 表ID在第4列（索引3）
            String tableName = row.getColumnValue(4); // 表名在第5列
            String tableComment = row.getColumnValue(5); // 表注释在第6列
            String tableLabel = row.getColumnValue(6); // 表标签在第7列

            String fieldName = row.getColumnValue(7); // 字段名在第9列
            String fieldComment = row.getColumnValue(8); // 字段注释在第10列

            // 获取或创建表信息
            TableInfo table = tableMap.computeIfAbsent(tableId,
                    id -> new TableInfo(id, tableName, tableComment, tableLabel));

            // 添加字段信息和原始行数据
            if (fieldName != null && !fieldName.isEmpty()) {
                table.addField(new OldFieldInfo(fieldName, fieldComment));
                table.addOriginalRow(row);
            }

            log.info("Table: {}, Field: {}, Comment: {}, progress: {}/{}", tableName, fieldName, fieldComment, i, allRows.size());
        }

        return new ArrayList<>(tableMap.values());
    }

    // 选择行数据（保留所有列信息）
    private static List<TableRowData> selectRows(List<TableRowData> allRows,
                                                 List<List<TableInfo>> tableGroups, int targetFieldCount) {
        List<TableRowData> selectedRows = new ArrayList<>();
        int currentFieldCount = 0;
        int groupIndex = 0;
        boolean forward = true; // 方向标志

        // 创建表ID到行的映射
        Map<String, List<TableRowData>> tableIdToRows = allRows.stream()
                .collect(Collectors.groupingBy(row -> row.getColumnValue(3)));

        while (currentFieldCount < targetFieldCount) {

            if (groupIndex < 0 || groupIndex >= tableGroups.size()) {
                // 到达一端，改变方向
                forward = !forward;
                groupIndex = forward ? 0 : tableGroups.size() - 1;
                continue;
            }

            List<TableInfo> currentGroup = tableGroups.get(groupIndex);
            if (!currentGroup.isEmpty()) {

                // 从当前组中选择一个表（选择第一个表）
                TableInfo selectedTable = currentGroup.get(0);
                String tableId = selectedTable.id;

                // 获取该表的所有行
                List<TableRowData> tableRows = tableIdToRows.get(tableId);
                if (tableRows != null) {
                    for (TableRowData row : tableRows) {
                        if (currentFieldCount < targetFieldCount) {
                            selectedRows.add(row);
                            currentFieldCount++;
                        } else {
                            break;
                        }
                    }
                }

                // 从组中移除已选择的表
                currentGroup.remove(0);

                log.info("Selected table: {}, currentFieldCount: {}", selectedTable.tableName, currentFieldCount);
            }

            // 移动到下一个组
            groupIndex = forward ? groupIndex + 1 : groupIndex - 1;
        }

        return selectedRows;
    }

    private static void writeSelectedRowsToExcel(List<TableRowData> selectedRows,
                                                 Sheet originalSheet, String outputPath) throws Exception {
        Workbook newWorkbook = new XSSFWorkbook();
        Sheet newSheet = newWorkbook.createSheet("Selected Fields");

        // 复制表头
        Row originalHeader = originalSheet.getRow(0);
        Row newHeader = newSheet.createRow(0);
        for (int i = 0; i < originalHeader.getLastCellNum(); i++) {
            Cell originalCell = originalHeader.getCell(i);
            Cell newCell = newHeader.createCell(i);
            if (originalCell != null) {
                switch (originalCell.getCellType()) {
                    case STRING: newCell.setCellValue(originalCell.getStringCellValue()); break;
                    case NUMERIC: newCell.setCellValue(originalCell.getNumericCellValue()); break;
                    case BOOLEAN: newCell.setCellValue(originalCell.getBooleanCellValue()); break;
                    default: newCell.setCellValue("");
                }
            }
        }

        // 写入数据行
        for (int i = 0; i < selectedRows.size(); i++) {
            TableRowData rowData = selectedRows.get(i);
            Row newRow = newSheet.createRow(i + 1);

            for (int col = 0; col < rowData.getColumnCount(); col++) {
                String value = rowData.getColumnValue(col);
                newRow.createCell(col).setCellValue(value);
            }
        }

        // 写入文件
        FileOutputStream fos = new FileOutputStream(outputPath);
        newWorkbook.write(fos);

        newWorkbook.close();
        fos.close();
    }

    // 计算表相似度并分组
    private static List<List<TableInfo>> groupSimilarTables(List<TableInfo> tables) {

        // 使用并查集算法进行分组
        UnionFind uf = new UnionFind(tables.size());

        // 两两比较表的相似度
        for (int i = 0; i < tables.size(); i++) {

            for (int j = i + 1; j < tables.size(); j++) {

                double similarity = calculateTableSimilarity(tables.get(i), tables.get(j));
                log.info("Table: {}, Table: {}, Similarity: {}, index: {}/{}", tables.get(i).tableName, tables.get(j).tableName, similarity, i, j);

                // 相似度阈值设为0.6（可根据实际情况调整）
                if (similarity >= 0.6) {
                    uf.union(i, j);
                }
            }
        }

        // 根据并查集结果分组
        Map<Integer, List<TableInfo>> groupMap = new HashMap<>();
        for (int i = 0; i < tables.size(); i++) {
            int root = uf.find(i);
            groupMap.computeIfAbsent(root, k -> new ArrayList<>()).add(tables.get(i));
        }

        return new ArrayList<>(groupMap.values());
    }

    // 计算两个表的相似度（综合考虑表名、表注释和字段信息）
    private static double calculateTableSimilarity(TableInfo t1, TableInfo t2) {

        // 1. 计算表名和表注释的相似度
        double nameSim = calculateStringSimilarity(t1.tableName, t2.tableName);
        double commentSim = calculateStringSimilarity(t1.tableComment, t2.tableComment);

        // 2. 计算字段相似度（基于字段名和字段注释）
        double fieldSim = calculateFieldSetSimilarity(t1.fields, t2.fields);

        // 综合相似度（权重可根据实际情况调整）
        return 0.3 * nameSim + 0.2 * commentSim + 0.5 * fieldSim;
    }

    // 计算两个字符串的相似度（使用Jaccard相似度）
    private static double calculateStringSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        if (s1.equals(s2)) return 1.0;

        Set<String> set1 = new HashSet<>(Arrays.asList(s1.split("\\s+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(s2.split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    // 计算两组字段的相似度
    private static double calculateFieldSetSimilarity(List<OldFieldInfo> fields1, List<OldFieldInfo> fields2) {
        if (fields1.isEmpty() && fields2.isEmpty()) return 1.0;
        if (fields1.isEmpty() || fields2.isEmpty()) return 0.0;

        // 计算字段名的相似度
        double nameSim = calculateTokenSetSimilarity(
                fields1.stream().map(f -> f.name).collect(Collectors.toList()),
                fields2.stream().map(f -> f.name).collect(Collectors.toList())
        );

        // 计算字段注释的相似度
        double commentSim = calculateTokenSetSimilarity(
                fields1.stream().map(f -> f.comment).collect(Collectors.toList()),
                fields2.stream().map(f -> f.comment).collect(Collectors.toList())
        );

        // 综合字段相似度
        return 0.6 * nameSim + 0.4 * commentSim;
    }

    // 计算两组token的相似度
    private static double calculateTokenSetSimilarity(List<String> tokens1, List<String> tokens2) {
        Set<String> set1 = tokens1.stream()
                .filter(t -> t != null && !t.isEmpty())
                .flatMap(t -> Arrays.stream(t.split("\\s+")))
                .collect(Collectors.toSet());

        Set<String> set2 = tokens2.stream()
                .filter(t -> t != null && !t.isEmpty())
                .flatMap(t -> Arrays.stream(t.split("\\s+")))
                .collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    // 将选中的字段写入Excel
    private static void writeSelectedFieldsToExcel(List<OldFieldInfo> fields, String outputPath) throws Exception {
        Workbook newWorkbook = new XSSFWorkbook();
        Sheet newSheet = newWorkbook.createSheet("Selected Fields");

        // 创建表头
        Row headerRow = newSheet.createRow(0);
        headerRow.createCell(0).setCellValue("字段名称");
        headerRow.createCell(1).setCellValue("字段注释");

        // 写入数据
        for (int i = 0; i < fields.size(); i++) {
            OldFieldInfo field = fields.get(i);
            Row row = newSheet.createRow(i + 1);
            row.createCell(0).setCellValue(field.name);
            row.createCell(1).setCellValue(field.comment);
        }

        // 写入文件
        FileOutputStream fos = new FileOutputStream(outputPath);
        newWorkbook.write(fos);

        newWorkbook.close();
        fos.close();
    }

    static class TableRowData {
        private List<String> columnValues = new ArrayList<>();

        public void addColumnValue(String value) {
            columnValues.add(value != null ? value : "");
        }

        public String getColumnValue(int index) {
            return index < columnValues.size() ? columnValues.get(index) : "";
        }

        public int getColumnCount() {
            return columnValues.size();
        }
    }

    // 表信息类（增加原始行数据）
    static class TableInfo {
        String id;
        String tableName;
        String tableComment;
        String tableLabel;
        List<OldFieldInfo> fields = new ArrayList<>();
        List<TableRowData> originalRows = new ArrayList<>();

        public TableInfo(String id, String tableName, String tableComment, String tableLabel) {
            this.id = id;
            this.tableName = tableName;
            this.tableComment = tableComment;
            this.tableLabel = tableLabel;
        }

        public void addField(OldFieldInfo field) {
            fields.add(field);
        }

        public void addOriginalRow(TableRowData row) {
            originalRows.add(row);
        }
    }

    // 字段信息类
    static class OldFieldInfo {
        String name;
        String comment;

        public OldFieldInfo(String name, String comment) {
            this.name = name;
            this.comment = comment;
        }
    }

    // 并查集类
    static class UnionFind {
        int[] parent;

        public UnionFind(int size) {
            parent = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                parent[rootY] = rootX;
            }
        }
    }

    // 获取单元格字符串值
    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            default: return "";
        }
    }

}