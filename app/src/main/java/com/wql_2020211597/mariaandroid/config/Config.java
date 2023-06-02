package com.wql_2020211597.mariaandroid.config;

public class Config {
    static private final String backendAddr = "http://10.128.170.37";
    static private final String backendPort = "9011";

    static private final String codeHeader = "<html>" +
            "<head>" +
            "<link href=\"file:///prism.css\" rel=\"stylesheel\" />" +
            "<style>" +
            "/* any additional CSS syles here */" +
            "</style>" +
            "</head>";


    public static String assetsDir(){
        return "file:///assets/";
    }
    public static String prettifierCode(String codeHtml) {
        String prettifiedCode = codeHeader + "<body>" + codeHtml + "</body" +
                "></html>";
        return prettifiedCode;
    }

    public static String getBackendAddr() {
        return backendAddr;
    }

    public static String getBackendPort() {
        return backendPort;
    }

    public static String getBackendUrl() {
        return backendAddr + ":" + backendPort;
    }
}
