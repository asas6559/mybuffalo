package com.ztesoft.remoteCall;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;

public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static HashMap<String, Method> MethodCache = new HashMap<>();
    private static HashMap<String, Object> ParsedSvc = new HashMap<>();
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> res = new HashMap<>();
        String svc = request.getRequestURI();
        svc = svc.substring(svc.lastIndexOf("/") + 1);
        svc = svc.substring(0, svc.lastIndexOf(".")) + "?" + svc.substring(svc.lastIndexOf(".") + 1) + "." + request.getQueryString();
        String data = getBody(request);
        System.out.println(svc);
        System.out.println(data);
        try {
            Object result = doRequest(svc, data);
            res.put("error", 0);
            res.put("res", result);
        } catch (Exception e) {
            res.put("error", 1);
            res.put("msg", e.getClass().getName() + ":" + e.getMessage());
        }
        response.setHeader("content-type", "text/xml;charset=utf8");
        response.getWriter().write(gson.toJson(res));
    }

    private static Object getService(String serviceName) throws Exception {
        Object svc = ParsedSvc.get(serviceName);
        if (svc != null) {
            // 每次从工厂中取一个新的.防止线程安全问题.
            svc = Class.forName(serviceName).newInstance();
            // svc= com.ztesoft.servicefactory.ServiceFactory.getService(serviceName);
        } else {
            svc = Class.forName(serviceName).newInstance();
            // 缓存method
            MethodCache.putAll(cacheMethod(svc.getClass()));
            // 缓存svc
            ParsedSvc.put(serviceName, svc);
        }
        return svc;
    }

    private static ArrayList<Object> getRequestArgs(String jsonArg, Method m) {
        ArrayList<Object> res = new ArrayList<>();
        String[] jsonsArgs = jsonArg.split("\n");
        Type[] typs = m.getGenericParameterTypes();
        for (int i = 0; i < typs.length; i++) {
            Object arg = gson.fromJson(jsonsArgs[i], typs[i]);
            res.add(arg);
        }
        return res;
    }

    protected static Object doRequest(String requestPath, String jsonArg) throws Exception {
        String[] svc = requestPath.split("\\?");
        Object service = getService(svc[0]);
        Method method = MethodCache.get(requestPath);
        if (null == method) {
            throw new Exception("no method : " + requestPath);
        }
        ArrayList<Object> args = getRequestArgs(jsonArg, method);
        Object result = method.invoke(service, args.toArray());
        return result;
    }

    private static String getArgs(Class<?> s) {
        if (s.isArray()) {
            return "array.";
        }
        String t = s.getName().toLowerCase().replace("java.lang.", "").replace("java.util.", "");
        switch (t) {
            case "long":
            case "int":
            case "double":
                return "number.";
            case "arraylist":
                return "array.";
            case "date":
            case "string":
                return t + ".";
            default:
                return "object.";
        }
    }

    private static HashMap<String, Method> cacheMethod(Class<?> clazz) throws Exception {
        HashMap<String, Method> res = new HashMap<String, Method>();
        for (Method m : clazz.getMethods()) {
            String mn = m.getName();
            if (mn.equals("wait") || mn.equals("equals") || mn.equals("toString") || mn.equals("hashCode") || mn.equals("getClass") || mn.equals("notify")
                    || mn.equals("notifyAll")) {
                continue;
            }
            String hs = clazz.getName() + "?" + mn + ".";
            Class<?>[] argTypes = m.getParameterTypes();
            for (Class<?> c : argTypes) {
                hs = hs + getArgs(c);
            }
            hs = hs.substring(0, hs.length() - 1);
            if (res.containsKey(hs)) {
                throw new Exception("repeated method, change the method name or parameter types. " + hs);
            }
            res.put(hs, m);
        }
        return res;
    }

    private static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }
}
