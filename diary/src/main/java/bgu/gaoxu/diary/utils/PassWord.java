package bgu.gaoxu.diary.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检测密码的合法性
 * 规则说明：
 * 1.密码不能含有空格字符串
 * 2.密码只能包括字母和数字
 */
public class PassWord {
    private String result = "false";

    public static void main(String[] args) {


    }

    public static String isValid(String password) {

        if (password.length() > 0) {
            if (password.length()<6){
                return "长度不足";
            }
            //判断是否有空格字符串
            for (int t = 0; t < password.length(); t++) {
                String b = password.substring(t, t + 1);
                if (b.equals(" ")) {
                    System.out.println("有空字符串");
                    return "有空字符串";
                }
            }


            //判断是否有汉字
            int count = 0;
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(password);
            while (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    count = count + 1;
                }
            }

            if (count > 0) {
                System.out.println("有汉字");
                return "有汉字";
            }


            //判断是否是字母和数字
            int numberCounter = 0;
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    return "需要字母和数字";
                }
                if (Character.isDigit(c)) {
                    numberCounter++;
                }
            }

        } else {
            return "不能为空";
        }
        return "true";
    }
}
