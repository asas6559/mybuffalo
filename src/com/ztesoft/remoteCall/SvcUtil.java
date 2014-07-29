package com.ztesoft.remoteCall;

import java.lang.reflect.Method;

public class SvcUtil {
    private static String getArgs(String s) {
        String t = s.toLowerCase().replace("java.lang.", "");
        switch (t) {
            case "long":
            case "int":
            case "double":
                return "number";
            case "string":
                return "string";
            case "date":
                return "date";
            case "arraylist":
                return "arraylist";
            default:
                return "object";
        }
    }

    public static String getMethodSignature(Class<?> clazz, Method m, boolean isShowObject) {
        String hs = clazz.getName() + "." + m.getName() + "(";
        Class<?>[] argTypes = m.getParameterTypes();
        for (Class<?> c : argTypes) {
            String cname = c.getName();
            hs = hs + (isShowObject ? getArgs(cname) : cname.substring(cname.lastIndexOf(".") + 1)) + ",";
        }
        hs += ")";
        hs = hs.replace(",)", ")");
        return hs;
    }
}
