import java.util.*;

// 多重加密
public class MutipleEn {
    // 整数转16bit二进制
    public static String b16(int v) {
        return String.format("%16s", Integer.toBinaryString(v)).replace(' ', '0');
    }

    //双重加密
    public static String DoubleEncode(String plaintext, String Keys) {
        String s = S_AES.EnCode(plaintext, Keys.substring(0, 16));
        return S_AES.EnCode(s, Keys.substring(16, 32));
    }

    //双重解密
    public static String DoubleUnCode(String ciphertext, String Keys) {
        String c1 = S_AES.UnCode(ciphertext, Keys.substring(16, 32));
        return S_AES.UnCode(c1, Keys.substring(0, 16));
    }

    // 中间相遇攻击
    public static void findKeyPairs(List<Pair<String, String>> pairs) {
        Pair<String, String> pair = pairs.get(0);
        Map<String, List<String>> map = new HashMap<>();
        //对第一对明密文的明文 对所有的k1 进行加密一次，并将结果存储
        for (int i = 0; i < Math.pow(2, 16); i++) {
            String k1 = b16(i);
            String encryptOnce = S_AES.EnCode(pair.first, k1);
            if (!map.containsKey(encryptOnce))
                map.put(encryptOnce, new ArrayList<>());
            map.get(encryptOnce).add(k1);// 对于一个明文，不同的key可能加密出相同的结果，所以Map的value为key的集合，Map的key为加密一次的结果
        }
        for (int j = 0; j < Math.pow(2, 16); j++) {
            String k2 = b16(j);
            String encryptOnce = S_AES.UnCode(pair.second, k2);
            if (map.containsKey(encryptOnce)) {//如果map中含有解密一次的结果，则找到一对密钥符合第一对明密文
                for (String k1 : map.get(encryptOnce)) {
                    boolean valid = true;
                    for (int k = 1; k < pairs.size(); k++) {//遍历所有数据，判断这对密钥是否符合其他明密文对
                        Pair<String, String> pair1 = pairs.get(k);
                        if (!S_AES.EnCode(pair1.first, k1).equals(S_AES.UnCode(pair1.second, k2))) {
                            valid = false;
                            break;// 有一对不适合，则直接跳出循环
                        }
                    }
                    if (valid)// 输出
                        System.out.println("找到密钥对：k1=" + k1 + " k2=" + k2);
                }
            }
        }
    }

    // 自定义类 用于存储明密文对
    private static class Pair<T, U> {
        public final T first;
        public final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    // 三重加密
    public static String TripleEncrypt(String plaintext, String Keys) {
        String s1 = S_AES.EnCode(plaintext, Keys.substring(0, 16));
        String s2 = S_AES.EnCode(s1, Keys.substring(16, 32));
        return S_AES.EnCode(s2, Keys.substring(32));
    }

    // 三重解密
    public static String TripleDecrypt(String ciphertext, String Keys) {
        String s1 = S_AES.UnCode(ciphertext, Keys.substring(32));
        String s2 = S_AES.UnCode(s1, Keys.substring(16, 32));
        return S_AES.UnCode(s2, Keys.substring(0, 16));
    }


    public static void main(String[] args) {
        /*String plain1 = "1100110000110011";
        String key1 = "0000000000000001" + "0000000000000011";
        System.out.println("双重加密：");
        System.out.println("明文：" + plain1);
        System.out.println("密钥：" + key1);
        String cipher1 = DoubleEncode(plain1, key1);
        System.out.println("加密结果：" + cipher1);
        System.out.println("解密结果：" + DoubleUnCode(cipher1, key1));*/

        // 中间相遇攻击
        /*List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("1100110000110011", "1110001111101101"));
        pairs.add(new Pair<>("1100110110110011", "1110111110101101"));
        pairs.add(new Pair<>("1010110110110011", "1100111110100010"));
        findKeyPairs(pairs);*/

        // 三重加解密
        String keys="1010010110010110" + "1110000110000111" + "1111000011000011";
        String plain2="1001100000111100";
        System.out.println("三重加密：");
        String cipher2=TripleEncrypt(plain2,keys);
        System.out.println("加密结果："+cipher2);
        System.out.println("解密结果："+TripleDecrypt(cipher2,keys));
        System.out.println(plain2.equals(TripleDecrypt(cipher2,keys)));
    }
}

