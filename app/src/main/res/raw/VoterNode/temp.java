import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class temp{
	public static void main(String arh[]) {
		try{
		HttpURLConnection conn = (HttpURLConnection) (new URL("http://192.168.0.1")).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		System.out.println("Connetion created ...");

		InputStream is = conn.getInputStream();
        StringBuffer buffer = new StringBuffer();;
                    byte []b = new byte[1];
                    while(is.read(b) !=-1){
                        buffer.append(new String(b,"UTF-8"));
                    }
                    if(buffer==null){
                    	System.out.println("buffer is null...");
                    }
                    conn.disconnect();
                    System.out.println(String.valueOf(buffer));
        }catch(Exception e){
        	System.out.println("Exception : "+e);
        }
	}
}