package server_req_resp;

/**
 * @author: 张涛
 * @date:   createDate：2017年10月20日 下午10:29:09   
 * @Description: 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
	public static final String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";
	
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final int PORT = 8080;
	private final int POOL_SIZE = 2;
	private ServerSocket serverSocket;
    private ExecutorService executorService;
	
    // 通道管理器
    private Selector selector;
    
    
    public MultiThreadServer() throws IOException {
        serverSocket = new ServerSocket(PORT, 1,
				InetAddress.getByName(IP_ADDRESS));
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * POOL_SIZE);
        System.out.println("服务已启动");
    }
	public static void main(String[] args) throws Exception {
		new MultiThreadServer().service();
	}

	public void service() throws Exception {
		while (true) {
			Socket socket=serverSocket.accept();
			executorService.execute(new Handler(socket));
			System.out.println("111");
		}
	}
}
