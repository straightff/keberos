package kb.ToolClass;

import java.util.UUID;

public class RandomKeyTool {
    // 30 位长度
    public static String createAuth(String ID1, String ID2) {
        String key1 = UUID.randomUUID().toString();
        String key2 = String.valueOf(System.currentTimeMillis());
        String key = ID1 + key1 + ID2 + key2;
        return MD5Tool.getMD5(key, 30);
    }

    public static String createServerKey() {
        String key1 = UUID.randomUUID().toString();
        String key2 = String.valueOf(System.currentTimeMillis());
        String key = key1 + key2;
        return MD5Tool.getMD5(key, 10);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(createServerKey());
        }

    }
}
