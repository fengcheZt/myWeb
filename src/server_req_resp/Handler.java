package server_req_resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.rmi.CORBA.Util;

public class Handler implements Runnable{
public static final String CHARCODE = "utf-8";
    
    private Socket socket;
    // shutdown command
 	private static final String SHUTDOWN_COMMAND = "\\SHUTDOWN";
    public Handler(Socket socket) {
        this.socket = socket;
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }

    public void run() {
        BufferedReader br = null;
        PrintWriter out = null;
		InputStream input = null;
		OutputStream output = null;
        try {
            br = getReader(socket);
            out = getWriter(socket);
            input = socket.getInputStream();
			output = socket.getOutputStream();
			// create Request object and parse
			Request request = new Request(input);
			request.parse(); // 从请求中读取内容
			System.out.println("shutdown.req=" + request.getUri());
			// create Response object
			Response response = new Response(output);
			response.setRequest(request);
			response.sendStaticResource();
			// Close the socket
			socket.close();
			// check if the previous URI is a shutdown command
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
