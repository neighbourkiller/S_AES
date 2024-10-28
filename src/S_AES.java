// S-AES普通加解密

public class S_AES {

    //密钥
    private static String[] Keys = new String[3];

    /**
     * 加密
     *
     * @param plaintext
     * @param keys
     * @return
     */
    public static String EnCode(String plaintext, String keys) {
        Keys = CodeUtils.extendKeys(keys);

        String ak = CodeUtils.Ak(plaintext, Keys[0]);

        String s = CodeUtils.subHalfByte(ak);
        String shiftRow = CodeUtils.shiftRow(s);
        String mixed = CodeUtils.mixColumns(shiftRow);
        String ak1 = CodeUtils.Ak(mixed, Keys[1]);

        String s1 = CodeUtils.subHalfByte(ak1);
        String shiftRow1 = CodeUtils.shiftRow(s1);
        String ak2 = CodeUtils.Ak(shiftRow1, Keys[2]);
        return ak2;
    }

    /**
     * 解密
     *
     * @param ciphertext
     * @param keys
     * @return
     */
    public static String UnCode(String ciphertext, String keys) {
        Keys = CodeUtils.extendKeys(keys);

        String ak = CodeUtils.Ak(ciphertext, Keys[2]);

        String shiftRow = CodeUtils.shiftRow(ak);
        String isubHalfByte = CodeUtils.IsubHalfByte(shiftRow);
        String ak1 = CodeUtils.Ak(isubHalfByte, Keys[1]);
        String s = CodeUtils.InverseMixColumns(ak1);

        String shiftRow1 = CodeUtils.shiftRow(s);
        String isubHalfByte1 = CodeUtils.IsubHalfByte(shiftRow1);
        return CodeUtils.Ak(isubHalfByte1, Keys[0]);
    }
}
