package com;

import java.lang.reflect.*;
import java.util.*;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.GOTO;

public class TestSvc {
    public String normalMethod(String a, String b, Long c) {
        return a + b + c;
    }

    public long normalMethod(int b, Long c) {
        System.out.println("here1");
        return b + c;
    }

    @Deprecated
    public <T extends TestPo1> String normalMethod(T t, long a) {
        return t.toString();
    }

    private String normalMethod(TestSvc t) {
        return t.toString();
    }

    public String normal(TestPo1 po) {
        return po.getHello() + po.getName();
    }

    public TestPo1 normal(TestPo1 po, String name) {
        po.setName(name);
        return po;
    }

    public ArrayList<TestPo1> normalMethod(TestPo1[] t, String a) {
        ArrayList<TestPo1> resString = new ArrayList<TestPo1>();
        for (TestPo1 po : t) {
            String tmpString = po.getHello();
            po.setHello(po.getName());
            po.setName(tmpString);
            resString.add(po);
        }
        return resString;
    }
    
    public TestPo1 normalMethod(HashMap<String, TestPo1> a) {
        Date date = Calendar.getInstance().getTime();
        Gson gson = new Gson();
        String jsonString = gson.toJson(date);
        System.out.println(jsonString);
        return a.get("haha");
    }

    public TestPo1[] normalbb(ArrayList<TestPo1> t, Long a,Long b,Long dd) {
        ArrayList<TestPo1> resString = new ArrayList<TestPo1>();
        for (TestPo1 po : t) {
            String tmpString = po.getHello();
            po.setHello(po.getName());
            po.setName(tmpString);
            resString.add(po);
        }
        TestPo1[] po1s = new TestPo1[resString.size()];
        resString.toArray(po1s);
        return po1s;
    }

    @Override
    public String toString() {
        return "hhahahah";
    };

    public static void main(String[] args) {
        Gson gson = new Gson();
        String jsonValue = "[{hello:\"safd\",name:\"zhy\"},{hello:\"safd2\",name:\"zhy2\"}]";
        String string = "5";
        Class clazz = TestSvc.class;
        Method[] ms = clazz.getMethods();
        for (Method m : ms) {
            if (m.getName() == "normalbb") {
                Type[] typs = m.getGenericParameterTypes();
                for(Type  t:typs) {
                    System.out.println(t.toString());
                    if(t instanceof ParameterizedTypeImpl) {
                        Object object = gson.fromJson(jsonValue, t);
                        System.out.println("yes:"+t.getClass().toString());
                    }else{
                        Object object = gson.fromJson(string,t);
                        System.out.println("no:"+t.getClass().toString());
                    };
                   // 
                }
            }
        }
        // ArrayList<String> resArrayList = new ArrayList<>();
        // TypeVariable<?>[] clazz = resArrayList.getClass().getTypeParameters();
        // for(TypeVariable t: clazz) {
        // System.err.println(t.getName());
        // }
        // System.out.println();
    }
}
