package com.ztesoft.remoteCall;

import java.lang.reflect.Method;


public class NetTest {
    
    public static void main(String[] args) throws Exception {
        Method m = NetTest.class.getMethod("dos", sun.class);
        m.invoke(new NetTest(),new sun());
        
        (new NetTest()).dos(new sun());
        
    }
public static <T extends father>  void dos(T t) {
    System.out.println("over here");
}
}


class father{}
class sun extends father{
    
}