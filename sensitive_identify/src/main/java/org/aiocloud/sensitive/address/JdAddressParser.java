import com.hankcs.hanlp.HanLP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class JdAddressParser {

    // 地址节点（省-市-区-街道-详情）
    static class AddressNode {
        String code;
        String name;
        Map<String, AddressNode> children = new HashMap<>();
    }

    private static final AddressNode root = new AddressNode();
    private static final Map<String, AddressNode> codeNodeMap = new HashMap<>();

    // 加载地址库
    public static void loadAllData() throws Exception {
        loadProvinces("D:/1@CodeManager/project/java-researcher/sensitive_identify/src/main/resources/address/provinces.csv");
        loadCities("D:/1@CodeManager/project/java-researcher/sensitive_identify/src/main/resources/address/cities.csv");
        loadAreas("D:/1@CodeManager/project/java-researcher/sensitive_identify/src/main/resources/address/areas.csv");
        loadStreets("D:/1@CodeManager/project/java-researcher/sensitive_identify/src/main/resources/address/streets.csv");
        loadVillages("D:/1@CodeManager/project/java-researcher/sensitive_identify/src/main/resources/address/villages.csv");
    }

    // 加载省份数据（示例格式：code,name）
    private static void loadProvinces(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 跳过 CSV 表头
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressNode node = new AddressNode();
                node.code = parts[0];
                node.name = parts[1];
                root.children.put(node.name, node);
                codeNodeMap.put(node.code, node);
            }
        }
    }

    // 加载城市数据（示例格式：code,name,province_code）
    private static void loadCities(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressNode node = new AddressNode();
                node.code = parts[0];
                node.name = parts[1];
                String provinceCode = parts[2];

                AddressNode provinceNode = codeNodeMap.get(provinceCode);
                if (provinceNode != null) {
                    provinceNode.children.put(node.name, node);
                    codeNodeMap.put(node.code, node);
                } else {
                    System.err.println("未找到省份节点: " + provinceCode);
                }
            }
        }
    }

    // 加载区县数据（示例格式：code,name,city_code）
    private static void loadAreas(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressNode node = new AddressNode();
                node.code = parts[0];
                node.name = parts[1];
                String cityCode = parts[2];

                AddressNode cityNode = codeNodeMap.get(cityCode);
                if (cityNode != null) {
                    cityNode.children.put(node.name, node);
                    codeNodeMap.put(node.code, node);
                }
            }
        }
    }

    // 加载街道数据（示例CSV格式：code,name,area_code）
    private static void loadStreets(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressNode node = new AddressNode();
                node.code = parts[0];
                node.name = parts[1];
                String areaCode = parts[2];

                AddressNode areaNode = codeNodeMap.get(areaCode);
                if (areaNode != null) {
                    areaNode.children.put(node.name, node);
                    codeNodeMap.put(node.code, node);
                }
            }
        }
    }

    // 加载村级数据（示例格式：code,name,street_code）
    private static void loadVillages(String filePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressNode node = new AddressNode();
                node.code = parts[0];
                node.name = parts[1];
                String streetCode = parts[2];

                AddressNode streetNode = codeNodeMap.get(streetCode);
                if (streetNode != null) {
                    streetNode.children.put(node.name, node);
                    codeNodeMap.put(node.code, node);
                }
            }
        }
    }

    // 根据 code 查找节点（需优化为 Map 缓存）
    private static AddressNode findNodeByCode(String code) {
        // 实现略（遍历树或预建 code->node 映射）
        return null;
    }

    /**
     * 动态规则引擎：多级地址补全
     *
     * @since 1.0.0
     *
     * @param: input
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-04-07 14:36 
     */
    public static String autoComplete(String input) {

        List<String> tokens = HanLP.segment(input).stream()
                .map(term -> term.word)
                .toList();

        AddressNode current = root;
        List<String> result = new ArrayList<>();

        for (String token : tokens) {

            // 1. 纠错处理（示例：拼音纠错）
            String corrected = correctTypo(token);
            // 2. 层级匹配
            AddressNode next = current.children.get(corrected);
            if (next == null) {
                // 3. 尝试模糊匹配（如"海淀" → "海淀区"）
                next = fuzzyMatch(current, corrected);
            }

            if (next != null) {
                result.add(next.name);
                current = next;
            } else {
                // result.add(corrected);
            }
        }

        // 4. 补全层级名称（如"北京" → "北京市"）
        return completeHierarchy(result, current);
    }

    // 模糊匹配（如区级补全）
    private static AddressNode fuzzyMatch(AddressNode parent, String token) {
        for (String name : parent.children.keySet()) {

            if (name.contains(token) || token.contains(name)) { // 部分匹配
                return parent.children.get(name);
            }

            // 拼音匹配（如"haidian" → "海淀区"）
            if (getPinyin(name).equals(getPinyin(token))) {
                return parent.children.get(name);
            }
        }
        return null;
    }

    // 层级补全（如补充"市"、"区"）
    private static String completeHierarchy(List<String> tokens, AddressNode lastNode) {

        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append(token);
        }

        if (lastNode != null) {
            String level = getLevel(lastNode);
            String name = lastNode.name;

            // 根据层级补全省略的后缀
            switch (level) {
                case "province":
                    if (!name.endsWith("省") && !name.endsWith("市") && !name.endsWith("自治区")) {
                        if (name.matches(".*[京津沪渝]")) {
                            // 直辖市补"市"
                            sb.append("市");
                        } else if (name.contains("新疆") || name.contains("西藏") || name.contains("内蒙古")) {
                            sb.append("自治区");
                        } else {
                            sb.append("省");
                        }
                    }
                    break;
                case "city":
                    if (!name.endsWith("市") && !name.endsWith("地区") && !name.endsWith("自治州")) {
                        // 默认补"市"
                        sb.append("市");
                    }
                    break;
                case "district":
                    if (!name.endsWith("区") && !name.endsWith("县") && !name.endsWith("市")) {
                        // 默认补"区"
                        sb.append("区");
                    }
                    break;
                case "street":
                    if (!name.endsWith("街道") && !name.endsWith("镇") && !name.endsWith("乡")) {
                        // 默认补"街道"
                        sb.append("街道");
                    }
                    break;
            }
        }

        return sb.toString();
    }

    private static String getLevel(AddressNode node) {

        if (node == null || node.code == null) return "unknown";

        // 清理非数字字符
        String code = node.code.replaceAll("[^0-9]", "");

        // 根据代码长度和结构判断层级
        switch (code.length()) {
            case 6:
                String provinceCode = code.substring(0, 2);
                String cityCode = code.substring(2, 4);
                String districtCode = code.substring(4);

                if (districtCode.equals("00")) {
                    if (cityCode.equals("00")) {
                        // 省级（如 110000）
                        return "province";
                    } else {
                        // 地级（如 110100）
                        return "city";
                    }
                } else {
                    // 县级（如 110108）
                    return "district";
                }
            case 9:
                // 乡级（如 110108001）
                return "street";
            case 12:
                // 村级（如 110108001001）
                return "village";
            default:
                return "unknown";
        }
    }

    // 简单纠错（示例：拼音纠错）
    private static String correctTypo(String token) {
        Map<String, String> commonTypos = Map.of(
                "海殿", "海淀",
                "北亰", "北京"
        );
        return commonTypos.getOrDefault(token, token);
    }

    private static String getPinyin(String chinese) {

        // 使用 HanLP 转换拼音（如"海淀" → "haidian"）
        return HanLP.convertToPinyinString(chinese, "", false);
    }

    public static void main(String[] args) throws Exception {

        loadAllData();

//        String input = "北京海淀中关村";
//        String output = autoComplete(input);
//        System.out.println(output);

        String input = "我住在美丽的杭州市西湖区文三路 100 号。这里交通十分便利，周边有许多商场和餐厅。离我家不远就是西溪国家湿地公园，那里风景如画，是休闲散步的好去处。我的朋友小明住在宁波市鄞州区中河街道堇山路 500 号，他经常邀请我去他家做客，我们会一起在附近的公园玩耍，或者去品尝当地的特色美食。";
        String output = autoComplete(input);
        System.out.println(output);
    }
}