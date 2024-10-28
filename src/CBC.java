import java.util.Arrays;
import java.util.Random;

public class CBC {
    private static final int BLOCK_SIZE = 16;
    private static String IV = "";

    // 整数转为 16 位二进制字符串
    private static String b16(int value) {
        return String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    // ASCII字符串转二进制数字符串
    private static String charToBS(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int x = str.charAt(i);
            result.append(b16(x));
        }
        return result.toString();
    }
    // 二进制数字符串转ASCII字符串数组
    private static String BStoChar(String str){
        StringBuilder result=new StringBuilder();
        for(int i=0;i<str.length();i+=8){
            String s=str.substring(i,i+8);
            char x = (char) Integer.parseInt(s, 2);
            result.append(x);
        }
        return result.toString();
    }

    // 生成初始向量
    public static void generateIV() {
       Random random=new Random();
       int iv=random.nextInt(1<<16);
       IV=b16(iv);
    }

    // CBC加密
    public static String cbcEncrypt(String plaintext, String key) {
        generateIV();
        StringBuilder ciphertext = new StringBuilder();
        String previousBlock = IV;
        for (int i = 0; i < plaintext.length() / BLOCK_SIZE; i++) {
            String block = "";
            if ((i + 1) * 16 > plaintext.length()) {
                block = plaintext.substring(i * BLOCK_SIZE);
                block = String.format("%16s", block).replace(' ', '0');
            } else block = plaintext.substring(i * 16, (i + 1) * 16);
            // 与前一个密文块异或
            block = CodeUtils.XOR(block, previousBlock);

            String encryptedBlock = S_AES.EnCode(block, key);
            ciphertext.append(encryptedBlock);
            previousBlock = encryptedBlock;
        }
//        return BStoChar(ciphertext.toString());
        return ciphertext.toString();
    }

    // CBC解密
    public static String cbcDecrypt(String ciphertext, String key) {
        String previousBlock = IV;
        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < ciphertext.length() / BLOCK_SIZE; i++) {
            String block = ciphertext.substring(i * 16, (i + 1) * 16);
            String decryptedBlock = S_AES.UnCode(block, key);
            // 异或
            decryptedBlock = CodeUtils.XOR(decryptedBlock, previousBlock);
            plaintext.append(decryptedBlock);
            previousBlock = block;
        }
        return BStoChar(plaintext.toString());
//        return plaintext.toString();
    }

    public static void main(String[] args) {
        String plaintext = "Hello, World!";
        String key="1011001101001010";
        String ciphertext=cbcEncrypt(charToBS(plaintext),key);
        System.out.println("CBC加密结果："+ciphertext);
        String ci="1001101000001000001011011011111011010000011001101111010000111011100000010111011011010101010000011000000110011100111010101101011010111001110010110100100000111010100101101101000101011011011110101010111111000100";
        System.out.println("CBC明文转码："+charToBS(plaintext));
        System.out.println("CBC解密结果："+cbcDecrypt(ciphertext,key));
    }
}
