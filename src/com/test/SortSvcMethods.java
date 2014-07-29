package com.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.ztesoft.remoteCall.SvcUtil;

public class SortSvcMethods {
    private static String folderPath = "C:\\project\\wsmp\\05_code\\wsmp\\src\\com";
    private static int repeatedMethod = 0;
    private static int okMethod = 0;

    public static void main(String[] args) throws Exception {
        File file = new File(folderPath);
        ArrayList<String> svcNames = getAllSvcNames(file);
        for (String s : svcNames) {
            checkSvc(Class.forName(s));
        }
        System.out.println("Svc count : " + svcNames.size());
        System.out.println("repeated method : " + repeatedMethod);
        System.out.println("ok method : " + okMethod);
    }

    public static void checkSvc(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        HashMap<String, String> methodSingaturesOfSvc = new HashMap<>();
        for (Method m : methods) {
            String mn = m.getName();
            if (mn.equals("wait") || mn.equals("equal") || mn.equals("toString") || m.equals("hashCode") || m.equals("getClass") || m.equals("notify") || m.equals("notifyAll")) {
                return;
            }
            String shortMethodsignature = SvcUtil.getMethodSignature(clazz, m, true);
            String fullMethodsignature = SvcUtil.getMethodSignature(clazz, m, false);
            if (methodSingaturesOfSvc.containsKey(shortMethodsignature)) {
                System.out.println("repeated : " + shortMethodsignature);
                System.out.println("\t" + methodSingaturesOfSvc.get(shortMethodsignature));
                System.out.println("\t" + fullMethodsignature);
                repeatedMethod++;
            } else {
                methodSingaturesOfSvc.put(shortMethodsignature, fullMethodsignature);
                okMethod++;
            }
        }
    }

    public static ArrayList<String> getAllSvcNames(File f) {
        ArrayList<String> res = new ArrayList<String>();
        if (f.isDirectory()) {// 文件夹
            for (File file : f.listFiles()) {
                res.addAll(getAllSvcNames(file));
            }
        } else if (f.isFile() && f.getName().contains("Svc")) {// 文件
            res.add("com" + f.getAbsolutePath().replace(folderPath, "").replace(".java", "").replace("\\", "."));
        }
        return res;
    }
}
