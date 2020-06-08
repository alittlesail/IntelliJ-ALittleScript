package plugin.generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructReflectInfo {
    public String content = "";        // 最终的结果
    public boolean generate = false;         // 是否生成

    public String name = "";           // 带命名域的结构体名
    public String ns_name = "";        // 命名域
    public String rl_name = "";        // 不再命名域的结构体名
    public int hash_code = 0;          // 哈希值
    public List<String> name_list = new ArrayList<>();    // 成员名列表
    public List<String> type_list = new ArrayList<>();    // 类型名列表
    public Map<String, String> option_map = new HashMap<>();    // 附加信息列表
}
