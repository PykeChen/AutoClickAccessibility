package com.cpy.tkaccessibility.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * IPHelp
 *
 * @author cpy
 * Created on 2022-08-09 10:49
 */
class IPHelp {

    private static String[] platforms = {
            "http://pv.sohu.com/cityjson",
            "http://pv.sohu.com/cityjson?ie=utf-8",
            "http://ip.chinaz.com/getip.aspx"
    };

    public static String getOutNetIP(Context context, int index) {
        if (index < platforms.length) {
            BufferedReader buff = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(platforms[index]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);//读取超时
                urlConnection.setConnectTimeout(5000);//连接超时
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                    InputStream is = urlConnection.getInputStream();
                    buff = new BufferedReader(new InputStreamReader(is, "UTF-8"));//注意编码，会出现乱码
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        builder.append(line);
                    }

                    buff.close();//内部会关闭 InputStream
                    urlConnection.disconnect();

                    Log.e("xiaoman", builder.toString());
                    if (index == 0 || index == 1) {
                        //截取字符串
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);//包含[satrtIndex,endIndex)
                        JSONObject jo = new JSONObject(json);
                        String ip = jo.getString("cip");

                        return ip;
                    } else if (index == 2) {
                        JSONObject jo = new JSONObject(builder.toString());
                        return jo.getString("ip");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return getIpAddress(context);
        }
        return getOutNetIP(context, ++index);
    }

    public static String getIpAddress(Context context){
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            }  else if (info.getType() == ConnectivityManager.TYPE_ETHERNET){
                // 有限网络
                return getLocalIp();
            }
        }
        return null;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    // 获取有限网IP
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "0.0.0.0";

    }

}
