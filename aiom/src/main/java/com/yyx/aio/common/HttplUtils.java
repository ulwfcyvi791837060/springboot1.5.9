package com.yyx.aio.common;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * 发送http请求
 * 
 * @author yaofeng
 * 
 */
public class HttplUtils {
	
	private final static String SIGNL="TTT";

	@SuppressWarnings("finally")
	public static synchronized String doGet(String urlStr) {

		StringBuffer st = new StringBuffer("");

		try {
			if (urlStr != null) {
				URL url = new URL(urlStr);// 生成url对象
				
				//解决服务器证书信任问题   start
	            trustAllHttpsCertificates();  
	            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
	            //解决服务器证书信任问题   end
	            
				URLConnection urlConnection = url.openConnection();// 打开url连接
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream(),"utf-8"));
				String line = null;

				while ((line = br.readLine()) != null) {
					// line.getBytes("UTF-8");
					// System.out.println(line);
					st.append(line);
				}
				br.close();
				
			}

		} catch (MalformedURLException e) {
			//System.out.println("连接到URL抛出异常信息：" + e);

		} catch (Exception e) {
			//System.out.println("连接到URL抛出异常信息：" + e);
		} finally {
			/*
			 * String s=""; try { s = new
			 * String(st.toString().getBytes("UTF-8"),"UTF-8"); } catch
			 * (UnsupportedEncodingException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }finally{ return s; }
			 */
			// System.out.println(st.toString());
			return st.toString();
		}

	}
	@SuppressWarnings("finally")
	public static String doPost(String urlStr, String param) {

		StringBuffer st = new StringBuffer("");

		try {
			if (urlStr != null) {
				URL url = new URL(urlStr);// 生成url对象
				
				//解决服务器证书信任问题   start
	            trustAllHttpsCertificates();  
	            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
	            //解决服务器证书信任问题   end
	            
				URLConnection  conn = (URLConnection )url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
//				conn.setRequestProperty("Content-Type", "application/octet-stream");
				conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				conn.setUseCaches(false);
				OutputStream outStream = conn.getOutputStream();
				byte[] resultArray = param.getBytes();
				outStream.write(resultArray);
				outStream.flush();
				outStream.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(),"utf-8"));
				String line = null;

				while ((line = br.readLine()) != null) {
					// line.getBytes("UTF-8");
					// System.out.println(line);
					st.append(line);
				}
				br.close();
				
			}

		} catch (MalformedURLException e) {
			System.out.println("连接到URL抛出异常信息：" + e);

		} catch (Exception e) {
			System.out.println("连接到URL抛出异常信息：" + e);
		} finally {
			/*
			 * String s=""; try { s = new
			 * String(st.toString().getBytes("UTF-8"),"UTF-8"); } catch
			 * (UnsupportedEncodingException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }finally{ return s; }
			 */
			// System.out.println(st.toString());
			return st.toString();
		}

	}
	
	static String ACTION_URL = "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=%1$f&y=%2$f";
	 
	/*public static BaiDuLonLat convertBaiDulonlat(double lon,double lat)
	{
		
		String urlString = String.format(ACTION_URL, lon, lat);
		BufferedReader br=null;
		URL url;
		try {
			url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();// 打开url连接
			br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String  line = br.readLine();
			br.close();
			if(line!=null&&line.trim().length()>0)
			{
				//System.out.println(line);
				BaiDuLonLat baiDuLonLat = JSON.parseObject(line, BaiDuLonLat.class);
				baiDuLonLat.setX(new String(decodeBase64(baiDuLonLat.getX())));
				baiDuLonLat.setY(new String(decodeBase64(baiDuLonLat.getY())));
				return baiDuLonLat;
			}else
			{
				return null;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}finally
		{
		}
	}*/
	public static byte[] decodeBase64(String input) throws Exception{   
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");   
        Method mainMethod= clazz.getMethod("decode", String.class);   
        mainMethod.setAccessible(true);   
         Object retObj=mainMethod.invoke(null, input);   
         return (byte[])retObj;   
    }  
	
	/*public static List<BaiDuLonLat> batchConvertBD(List<BaiDuLonLat> list)
	{
		ArrayBlockingQueue<BaiDuLonLat> arrayBlockingQueue=new ArrayBlockingQueue<BaiDuLonLat>(10000);
		List<BaiDuLonLat> rslist=new ArrayList<BaiDuLonLat>();
		for(int i=0;i<list.size();i++)
		{
			BaiDuLonLat src=list.get(i);
			new ConvertThread(src, arrayBlockingQueue).start();
		}
		int count=list.size();
		while(true)
		{
			if(rslist.size()==count)
			{
				break;
			}
			
			try {
				BaiDuLonLat tmpbdl=arrayBlockingQueue.take();
				if(tmpbdl.getId() == -1)
				{
					break;
				}
				rslist.add(tmpbdl);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		Collections.sort(rslist, new Comparator<BaiDuLonLat>() {

			public int compare(BaiDuLonLat o1, BaiDuLonLat o2) {
				return o1.getId()-o2.getId();
			}
		} );
		return rslist;
	}
	private static class ConvertThread extends Thread{
		BaiDuLonLat baiDuLonLat=null;
		ArrayBlockingQueue<BaiDuLonLat> arrayBlockingQueue=null;
	 	public ConvertThread(BaiDuLonLat baiDuLonLat,ArrayBlockingQueue<BaiDuLonLat> arrayBlockingQueue)
		{
			this.baiDuLonLat=baiDuLonLat;
			this.arrayBlockingQueue=arrayBlockingQueue;
		}
		public void run()
		{
			double lon =Double.parseDouble(baiDuLonLat.getX());
			double lat =Double.parseDouble(baiDuLonLat.getY());
			
			BaiDuLonLat bdll=convertBaiDulonlat(lon,lat);
			bdll.setId(this.baiDuLonLat.getId());
			try {
				synchronized (SIGNL) {
					arrayBlockingQueue.put(bdll);
				}
				
			} catch (Exception e) {
				BaiDuLonLat exit=new BaiDuLonLat();
				exit.setId(-1);
				try {
					arrayBlockingQueue.put(exit);
				} catch (InterruptedException e1) {
				}
				e.printStackTrace();
			}
			
		}
	}*/

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
		System.out.println("111=="+doGet("http://192.168.110.108/vehicle_pic/P0/n9107871_2.jpg"));
	}

}
