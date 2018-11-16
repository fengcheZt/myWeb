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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadNIOServer {
	public static final String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";
	
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final int PORT = 8080;
	private final int POOL_SIZE = 2;
	private ServerSocket serverSocket;
    private ExecutorService executorService;
	private ServerSocketChannel serverChannel;
    // 通道管理器
    private Selector selector;
    
    
    public MultiThreadNIOServer() throws IOException {
    	// 获得一个ServerSocket通道
        this.serverChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        this.serverChannel.configureBlocking(false);
        // 将该通道对于的serverSocket绑定到port端口
        this.serverChannel.socket().bind(new InetSocketAddress(PORT));
        // 获得一个通道管理器(选择器)
        this.selector = Selector.open();
        /*
         * 将通道管理器和该通道绑定，并为该通道注册selectionKey.OP_ACCEPT事件
         * 注册该事件后，当事件到达的时候，selector.select()会返回，
         * 如果事件没有到达selector.select()会一直阻塞
         */
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
	public static void main(String[] args) throws Exception {
		new MultiThreadNIOServer().listen();
	}

	public void listen() throws IOException {
			while (true) {
				// 等待请求，每次等待阻塞15s，超过3s后县城继续向下运行，如果传入0或者不传参数则一直阻塞
				if (0 == this.selector.select(15000)) {
					System.out.println("等待超时。。。继续执行");
					continue;
				}
				// 获得selector中选中的相的迭代器，选中的相为注册的事件
	            Iterator ite = this.selector.selectedKeys().iterator();
	            while (ite.hasNext()) {
	                SelectionKey key = (SelectionKey) ite.next();
	                new Thread(new HttpHandler(key)).run();
	                ite.remove();
	            }
			}
		
		
	}
}
