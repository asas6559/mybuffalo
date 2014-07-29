package com.ztesoft.remoteCall;

import java.lang.reflect.Method;

public class ms {
    private Method method;
    public ms(Method m) {
        method = m;
    }
    private ms() {}
    @Override
    public int hashCode() {
        String name = method.getName();
        Class[] argTypes = method.getParameterTypes();
        int typeHaString = 0;
        for(Class c:argTypes) {
            typeHaString+= c.getName().hashCode();
        }
        return name.hashCode()+typeHaString;
    }
    
}
