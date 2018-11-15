package server_req_resp;

import java.io.DataOutputStream;
/**
 * @author: 焦
 * @date:   createDate：2017年10月20日 下午10:30:22   
 * @Description: 
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


public class Response {
     private static final int BUFFER_SIZE = 1024;
     Request request;
     OutputStream output;
     DataOutputStream sout;
     public Response(OutputStream output) {
           this.output = output;
           sout=new DataOutputStream(output);
     }
     public void setRequest(Request request) {
           this.request = request;
     }
      
     public void sendStaticResource()  {
          
           FileInputStream fis = null;
           try {
        	   File file = null;
        	   	if("\\download".equals(request.getUri())){
        	   		file = new File(MultiThreadServer.WEB_ROOT, "mfrsOut.war");
        	   	}else{
        	   		file = new File(MultiThreadServer.WEB_ROOT, request.getUri());
        	   	}
                
                
                System.out.println("*************Http-response****************");
                if (file.exists()) {
                	long last=file.lastModified();
//                	JSONObject object=JSONObject.fromObject(request.toString()); 
//                    long requestLast=Long.valueOf(object.getString("If-Modified-Since"));   
                    String line="";
                    if(last==request.getRequestLast()){
                		line="HTTP/1.1 304 OK \r\n";  
                	}else{
                		line="HTTP/1.1 200 OK \r\n";  
                	}
                	 System.out.print("line="+line);
                     //sout.writeChars(line); 
                     sout.write(line.getBytes());//用字节传输，不能用字符，浏览器无法解析
                     String header="";
                     if("\\download".equals(request.getUri())){
                    	 header="Content-disposition: attachment;; filename=mfrsOut.war \r\n"
                      		 	 +"Content-Type: application/x-msdownload"+" \r\n"
                    			 +"Content-length: "+file.length()+" \r\n";
                     }else{
                    	 header="Content-Type: text/html; charset=utf-8 \r\n"
                      		 	  +"Content-length: "+file.length()+" \r\n"
                            +"Cache-Control: no-cache "+" \r\n"
                            +"Last-Modified: "+last+" \r\n\r\n";
                     }
                     
                     System.out.print("header="+header);             
                     sout.writeBytes(header);
                     if(last!=request.getRequestLast()){
                    	 fis = new FileInputStream(file);                     
                         byte[] bytes = new byte[BUFFER_SIZE];
                         int ch = fis.read(bytes, 0, BUFFER_SIZE);                                      
                         while (ch!=-1) { //ch==-1表示读到末尾了                    	
                             sout.write(bytes, 0, ch); //写出到浏览器                        
                             System.out.print(Arrays.toString(bytes));
                             ch = fis.read(bytes, 0, BUFFER_SIZE);//再读会接上一次读的位置往下读，如果读到末尾就会返回-1，终止输出
                         }
                     }
                     sout.close();
                } else {
                     // file not found
                     String errorMessage = "HTTP/1.1 404 File Not Found \r\n" 
                    		 			 + "Content-Type: text/html \r\n" 
                    		 			 + "Content-Length: 23 \r\n" 
                    		 			 + "\r\n" 
                    		 			 + "<h1>File Not Found</h1>";
                     System.out.println(errorMessage);
                     output.write(errorMessage.getBytes());
                }
           }catch (Exception e) {
        	   e.printStackTrace();
           } finally {
                if (fis!=null)
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
           }
     }
}