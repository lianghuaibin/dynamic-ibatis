/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.utils;

/**
 * 命名工具类
 *
 * @author suntao1
 * @since 2016/01/19
 */
public class CamelCaseUtils {
    private static final char SEPARATOR = '_';

    /**
     * 驼峰命名修改为下划线命名
     *
     * @param camelName
     * @return
     */
    public static String toUnderlineName(String camelName) {
        return toUnderlineName(camelName,false);
    }

    /**
     * 驼峰命名修改为下划线命名
     *
     * @param camelName
     * @param ignoreDigit 为true，则遇到数字时，不在前面加_
     * @return
     */
    public static String toUnderlineName(String camelName,boolean ignoreDigit) {
        if (camelName == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < camelName.length(); i++) {
            char c = camelName.charAt(i);

            boolean nextUpperCase = true;

            if (i < (camelName.length() - 1)) {
                nextUpperCase = Character.isUpperCase(camelName.charAt(i + 1)) || Character.isDigit(camelName.charAt(i + 1));
            }

            if ((i >= 0) && (Character.isUpperCase(c) || (!ignoreDigit && Character.isDigit(c)))) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 下划线名称修改为首字母为小写的驼峰
     *
     * @param underLineName
     * @return
     */
    public static String toCamelCase(String underLineName) {
        if (underLineName == null) {
            return null;
        }

        underLineName = underLineName.toLowerCase();

        StringBuilder sb = new StringBuilder(underLineName.length());
        boolean upperCase = false;
        for (int i = 0; i < underLineName.length(); i++) {
            char c = underLineName.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 下划线名称转为首字母为大写的驼峰
     *
     * @param underLineName
     * @return
     */
    public static String toCamelCaseCapitalize(String underLineName) {
        if (underLineName == null) {
            return null;
        }
        underLineName = toCamelCase(underLineName);
        return underLineName.substring(0, 1).toUpperCase() + underLineName.substring(1);
    }

    public static void main(String[] args){
        String underLineName = CamelCaseUtils.toUnderlineName("FinFeeDetail114Test");
        System.out.println(underLineName);
        underLineName = CamelCaseUtils.toUnderlineName("FinFeeDetail114Test",true);
        System.out.println(underLineName);
    }

}
