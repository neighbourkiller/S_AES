//功能类

public class CodeUtils {

    private static final String[][] S_Box = new String[][]{
            {"1001", "0100", "1010", "1011"},
            {"1101", "0001", "1000", "0101"},
            {"0110", "0010", "0000", "0011"},
            {"1100", "1110", "1111", "0111"}
    };
    private static final String[][] IS_Box = new String[][]{
            {"1010", "0101", "1001", "1011"},
            {"0001", "0111", "1000", "1111"},
            {"0110", "0000", "0010", "0011"},
            {"1100", "0100", "1101", "1110"}
    };
    private static final String[][] S = new String[][]{
            {"0001", "0100"},
            {"0100", "0001"}
    };
    private static final String[][] IS = new String[][]{
            {"1001", "0010"},
            {"0010", "1001"}
    };
    // RCON
    private static final String[] RC = new String[]{
            "10000000", "00110000"
    };

    // 异或
    public static String XOR(String s1, String s2) {
        if (s1.length() != s2.length())
            return "error";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s1.length(); i++) sb.append(s1.charAt(i) == s2.charAt(i) ? '0' : '1');
        return sb.toString();
    }

    // 将二进制字符串转为整数
    private static int binaryStringToInt(String binaryStr) {
        return Integer.parseInt(binaryStr, 2);
    }

    //16bit转 2*2 4-bit矩阵
    private static String[][] stringToMatrices(String s) {
        return new String[][]{
                {s.substring(0, 4), s.substring(8, 12)},
                {s.substring(4, 8), s.substring(12, 16)}
        };
    }

    // GF2^4内的乘法
    private static String GF2_4(String a, String b) {
        // 不可约多项式 x^4 + x + 1，二进制为 10011
        int MODULO = 0b10011;
        int numA = binaryStringToInt(a); // 将二进制字符串转为整数
        int numB = binaryStringToInt(b); // 将二进制字符串转为整数
        int product = 0;
        // 逐位相乘
        while (numB > 0) {
            // 如果当前位是1，则加上 numA
            if ((numB & 1) != 0) {
                product ^= numA; // 按位异或累加
            }
            // 左移 numA，相当于乘以2
            numA <<= 1;
            // 检查是否超出 4-bit，进行模运算
            if ((numA & 0b10000) != 0) { // 如果第5位超出
                numA ^= MODULO; // 模不可约多项式 10011
            }
            // 右移 numB
            numB >>= 1;
        }
        // 将结果转换为 4-bit 二进制字符串并返回
        return String.format("%4s", Integer.toBinaryString(product)).replace(' ', '0');
    }

    // 矩阵乘法
    private static String multiplyMatrices(String[][] matA, String[][] matB) {
        int rowsA = matA.length;
        int colsA = matA[0].length;
        int rowsB = matB.length;
        int colsB = matB[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("矩阵无法相乘");
        }
        String[][] result = new String[2][2];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                String temp = "0000";
                for (int k = 0; k < colsA; k++) {
                    temp = XOR(temp, GF2_4(matA[i][k], matB[k][j]));
                }
                result[i][j] = temp;
            }
        }
        return result[0][0]+result[1][0]+result[0][1]+result[1][1];
    }

    // g函数
    private static String g(String w, int i) {
        String r0 = w.substring(0, 4);
        String l0 = w.substring(4, 8);
        StringBuilder temp = new StringBuilder();
        temp.append(S_Box[binaryStringToInt(l0.substring(0, 2))][binaryStringToInt(l0.substring(2, 4))]);
        temp.append(S_Box[binaryStringToInt(r0.substring(0, 2))][binaryStringToInt(r0.substring(2, 4))]);
        return XOR(temp.toString(), RC[i]);
    }

    // 密钥加
    public static String Ak(String plaintext, String keys) {
        String s = XOR(plaintext, keys);
        return s;
    }
    private static String subHalfImpl(String s,String[][] Box){
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < 16; i += 4) {
            int row = binaryStringToInt(s.substring(i, i + 2));
            int col = binaryStringToInt(s.substring(i + 2, i + 4));
            temp.append(Box[row][col]);
        }
        return temp.toString();
    }

    // 半字节替换
    public static String subHalfByte(String s) {
        return subHalfImpl(s,S_Box);
    }

    // 逆半字节替换
    public static String IsubHalfByte(String s) {
        return subHalfImpl(s,IS_Box);
    }

    // 行移位
    public static String shiftRow(String s) {
        return s.substring(0, 4) + s.substring(12, 16) + s.substring(8, 12) + s.substring(4, 8);
    }

    // 列混淆
    public static String mixColumns(String s) {
        return multiplyMatrices(S, stringToMatrices(s));
    }

    // 逆列混淆
    public static String InverseMixColumns(String s) {
        return multiplyMatrices(IS, stringToMatrices(s));
    }

    // 密钥扩展
    public static String[] extendKeys(String keys) {
        String[] w = new String[6];
        w[0] = keys.substring(0, 8);
        w[1] = keys.substring(8, 16);
        w[2] = XOR(w[0], g(w[1], 0));
        w[3] = XOR(w[2], w[1]);
        w[4] = XOR(w[2], g(w[3], 1));
        w[5] = XOR(w[4], w[3]);
        return new String[]{w[0] + w[1], w[2] + w[3], w[4] + w[5]};
    }

    public static void main(String[] args) {
//        multiplyMatrices(S, )
    }
}
