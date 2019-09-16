package nju.citix.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;

public class IPUtil {
    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * ping指定地址
     *
     * @param ipAddress 目的地址
     * @return 2s内能ping通则返回true
     */
    public static boolean ping(String ipAddress) {
        int timeOut = 2000;  //超时应该在2s以上
        try {
            return InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (IOException e) {
            return false;
        }
    }

}