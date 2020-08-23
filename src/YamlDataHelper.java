//0813   testng 使用Yaml做数据驱动

import org.testng.annotations.DataProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlDataHelper {

    private static List<Map<String, String>> getYamlList() {
        List<Map<String, String>> list = new ArrayList();
        Map<String, Map<String, String>> map = readYamlUtil();
        for (Map.Entry<String, Map<String, String>> me : map.entrySet()) {
            Map<String, String> numNameMapValue = me.getValue();
            Map<String, String> tmp = new HashMap<>();
            for (Map.Entry<String, String> nameMapEntry : numNameMapValue.entrySet()) {
                String numKey = nameMapEntry.getKey();
                String nameValue = nameMapEntry.getValue();
                tmp.put(numKey, nameValue);
            }
            list.add(tmp);
        }
        return list;
    }
    public static Map<String, Map<String, String>> readYamlUtil() {
        Map<String, Map<String, String>> map = null;
        try {
            Yaml yaml = new Yaml();
            URL url = YamlDataHelper.class.getClassLoader().getResource("application.yaml");
            if (url != null) {
                //获取yaml文件中的配置数据，然后转换为Map
                map = yaml.load(new FileInputStream(url.getFile()));
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;

    }
    @DataProvider
    public Object[][] yamlDataMethod() {
        List<Map<String, String>> yamlList = getYamlList();
        Object[][] files = new Object[yamlList.size()][];
        for (int i = 0; i < yamlList.size(); i++) {
            files[i] = new Object[]{yamlList.get(i)};
        }
        return files;
    }


}
