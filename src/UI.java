import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class UI extends JFrame {
    // 数据类型
    enum DataType {
        Binary, ASCII
    }

    private static DataType inputType = DataType.Binary;
    private static JTextField inputPlaintext;
    private static JTextField inputKeys;
    private static JTextField inputCiphertext;

    public UI() {
        setTitle("S-AES");
        // 设置窗体大小
        setSize(400, 300);
//        setLayout(new GridLayout(3, 3));
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("src/sgs.png").getImage());
        inputPlaintext = new JTextField(20);
        inputPlaintext.setBounds(100, 50, 200, 50);
        inputKeys = new JTextField(48);
        inputKeys.setBounds(100, 100, 200, 50);
        inputCiphertext = new JTextField(20);
        inputCiphertext.setBounds(100, 150, 200, 50);

        JLabel l1 = new JLabel("明文");
        l1.setBounds(50, 50, 50, 50);
        JLabel l2 = new JLabel("密钥");
        l2.setBounds(50, 100, 50, 50);
        JLabel l3 = new JLabel("密文");
        l3.setBounds(50, 150, 50, 50);


        JButton btnEn = new JButton("加密");
        btnEn.setBounds(50, 210, 100, 50);
        JButton btnUn = new JButton("解密");
        btnUn.setBounds(250, 210, 100, 50);
//        JButton btnClear = new JButton("清除");
        JRadioButton btnChange = new JRadioButton("ASCII");
        btnChange.setBounds(300, 50, 100, 50);

        btnChange.addActionListener(e -> {
            if (btnChange.isSelected())
                inputType = DataType.ASCII;
            else inputType = DataType.Binary;
        });

        btnEn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = inputPlaintext.getText();
                String keys = inputKeys.getText();
                if (testText(code, keys)) {
                    if (inputType == DataType.ASCII) {
                        int x = code.charAt(0);
                        int y = code.charAt(1);
                        code = b8(x) + b8(y);
                    }
                    String s = S_AES.EnCode(code, keys);
                    if (inputType == DataType.ASCII) {
                        char x = (char) Integer.parseInt(s.substring(0, 8), 2);
                        char y = (char) Integer.parseInt(s.substring(8, 16), 2);
                        s = x + String.valueOf(y);
                    }
                    inputCiphertext.setText("加密结果：" + s);
                }
            }
        });
        btnUn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = inputCiphertext.getText();
                String keys = inputKeys.getText();
                if (testText2(code, keys)) {
                    String s = S_AES.UnCode(code, keys);
                    inputPlaintext.setText("解密结果：" + s);
                }
            }
        });

        add(l1);
        add(inputPlaintext);
        add(btnChange);
        add(l2);
        add(inputKeys);
        add(l3);
        add(inputCiphertext);
        add(btnEn);
        add(btnUn);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static Boolean testText(String t1, String t2) {
        if (inputType == DataType.ASCII)
            return testText1(t1, t2);
        else return testText2(t1, t2);
    }

    private static Boolean testText1(String t1, String t2) {
        if (t1.length() == 2 && t2.length() == 16) {
            for (int c : t1.toCharArray()) {
                if (c <= 0 || c >= 127 || !t2.matches("[01]+"))
                    return false;
            }
            return true;
        } else return false;
    }

    private static Boolean testText2(String t1, String t2) {
        if (t1.length() == 16 && t2.length() == 16) {
            return t1.matches("[01]+") && t2.matches("[01]+");
        }
        return false;
    }

    private static String b8(int v) {
        return String.format("%8s", Integer.toBinaryString(v)).replace(' ', '0');
    }
}
