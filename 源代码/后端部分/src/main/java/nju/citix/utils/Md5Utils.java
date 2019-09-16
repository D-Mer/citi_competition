package nju.citix.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

/**
 * @author DW
 * @date 2019/8/15
 */
public class Md5Utils {
    public static final String LEN_ERROR = "长度错误";

    /**
     * 进行md5加密
     *
     * @param word 被加密的字符串
     * @param salt 12位长的盐
     * @return 加密的结果
     */
    @NotNull
    public static String encode(@NotNull String word, @NotNull String salt) {
        Assert.state(salt.length() == 12, LEN_ERROR);

        final String md5 = DigestUtils.md5DigestAsHex((word + salt).getBytes());
        StringBuilder s = new StringBuilder(44);
        for (int i = 0; i < 16; i++) {
            s.append(md5.charAt(i * 2)).append(md5.charAt(i * 2 + 1));
            if ((i + 1) % 4 != 0) {
                s.append(salt.charAt(i / 4 * 3 + i % 4));
            }
        }
        return s.toString();
    }

    public static boolean check(@NotNull String code, @NotNull String word, @NotNull String salt) {
        Assert.state(code.length() == 44, LEN_ERROR);

        StringBuilder saltCalculated = new StringBuilder(12);
        StringBuilder md5Calculated = new StringBuilder(32);
        for (int i = 0; i < 4; i++) {
            char[] temp = new char[11];
            code.getChars(11 * i, 11 * i + 11, temp, 0);
            for (int j = 1; j < 12; j++) {
                if (j % 3 == 0) {
                    saltCalculated.append(temp[j - 1]);
                } else {
                    md5Calculated.append(temp[j - 1]);
                }
            }
        }
        return salt.contentEquals(saltCalculated) && encode(word, salt).contentEquals(code)
                && DigestUtils.md5DigestAsHex((word + salt).getBytes()).contentEquals(md5Calculated);
    }
}
