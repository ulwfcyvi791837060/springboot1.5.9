package com.yyx.aio.common;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

/*
 * @fun 模拟http请求
 * @author yaofeng
 *
 */
public class HttpUtil {

	public static boolean proxySet = false;
	
    public static String proxyHost = "127.0.0.1";
    
    public static int proxyPort = 8087;
    
	 /*
     * 发起http请求获取返回结果 
     * @param req_url 请求地址 
     * @return 
     */ 
    public static synchronized String httpRequest(String req_url) {
        StringBuffer buffer = new StringBuffer();  
        
        InputStream inputStream = null;
        InputStreamReader inputStreamReader =null;
        BufferedReader bufferedReader = null;
        HttpURLConnection httpUrlConn = null;
        
        try {  
            URL url = new URL(req_url);  
            
            //解决服务器证书信任问题   start
            trustAllHttpsCertificates();  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
            //解决服务器证书信任问题   end
            
            httpUrlConn = (HttpURLConnection) url.openConnection();  
            //httpUrlConn.setDoOutput(false);  
            //====设置是否从httpUrlConnection读入，默认情况下是true;   
            httpUrlConn.setDoInput(true); ;  
            //Post 请求不能使用缓存 
            httpUrlConn.setUseCaches(false); 
//          httpUrlConn.setRequestMethod("GET");
            //设置连接主机超时（单位：毫秒） 
            httpUrlConn.setConnectTimeout(50);
            //设置从主机读取数据超时（单位：毫秒） 
            httpUrlConn.setReadTimeout(50);
            httpUrlConn.connect();  
            
//            System.out.println("111=="+httpUrlConn.getResponseCode());
            
            if (httpUrlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("发送请求异常===="+req_url);
                return null;
            }
            
            //发送http请求，将返回的输入流转换成字符串  
            inputStream = httpUrlConn.getInputStream();  
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            bufferedReader = new BufferedReader(inputStreamReader);  
   
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            httpUrlConn.disconnect(); 
        } catch (Exception e) {  
        	System.out.println("文件找不到："+req_url);
//        	e.printStackTrace();
            return null;
        } finally {
        	try {
        		// 释放资源  
        		if (null != bufferedReader) {
            		bufferedReader.close();  
            	}
        		if (null != inputStreamReader) {
        			inputStreamReader.close();  
            	}
        		if (null != inputStream) {
        			inputStream.close();  
            	}
        		if (null != bufferedReader) {
            		bufferedReader.close();  
            	}
                inputStream = null;  
                httpUrlConn.disconnect(); 
                
			} catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("关闭连接出问题了===");
			}
        }
        return buffer.toString();  
    }  
       
    /*
     * 发送http请求取得返回的输入流 
     * @param requestUrl 请求地址 
     * @return InputStream 
     */ 
    public static InputStream httpRequestIO(String requestUrl) {  
        InputStream inputStream = null;  
        try {  
            URL url = new URL(requestUrl);  
           
            //解决服务器证书信任问题   start
            trustAllHttpsCertificates();  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
            //解决服务器证书信任问题   end
            
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setRequestMethod("GET");  
            httpUrlConn.connect();  
            // 获得返回的输入流  
            inputStream = httpUrlConn.getInputStream();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return inputStream;  
    }
     
     
    /*
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            
            //解决服务器证书信任问题   start
            trustAllHttpsCertificates();  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
            //解决服务器证书信任问题   end
            
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
 
    /*
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param isproxy
     *               是否使用代理模式
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,boolean isproxy) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            
            //解决服务器证书信任问题   start
            trustAllHttpsCertificates();  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
            //解决服务器证书信任问题   end
            
            HttpURLConnection conn = null;
            if(isproxy){//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) realUrl.openConnection(proxy);
            }else{
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            // 打开和URL之间的连接
             
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");    // POST方法
             
             
            // 设置通用的请求属性
             
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
             
            conn.connect();
             
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
     
    static HostnameVerifier hv = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
			return true;
		}
	};

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
  
    static class miTM implements javax.net.ssl.TrustManager,  
            javax.net.ssl.X509TrustManager {  
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
  
        public boolean isServerTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public boolean isClientTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public void checkServerTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
  
        public void checkClientTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
    }  
    
    public static void main(String[] args) {
//        //demo:代理访问
//        String url = "http://api.adf.ly/api.php";
//        String para = "key=youkeyid&youuid=uid&advert_type=int&domain=adf.ly&url=http://somewebsite.com";
    	
//         String picIsWrite=HttpUtil.httpRequest(url+ "/"+map.get("batch") +"/"+ map.get("pic1")+".jpg");
    	
//        String sr=HttpUtil.sendPost(url,para,true);
//        System.out.println(sr);
    	
    	String url="http://192.168.110.108/vehicle_pic/R4/p41511632.jpg";
    	String sr=HttpUtil.httpRequest(url);
    	System.out.println(sr);
    }
    
	
}
