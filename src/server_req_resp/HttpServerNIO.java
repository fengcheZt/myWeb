package server_req_resp;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

public class HttpServerNIO {
	public static void main(String args[]) {
		try (ServerSocketChannel channel = ServerSocketChannel.open();) {
			channel.bind(new InetSocketAddress(9999));
			channel.configureBlocking(false);// 非阻塞
			Selector sel = Selector.open();
			channel.register(sel, SelectionKey.OP_ACCEPT);
			// 创建处理器
			while (true) {
				// 等待请求，每次等待阻塞15s，超过3s后县城继续向下运行，如果传入0或者不传参数则一直阻塞
				if (0 == sel.select(15000)) {
					Logger.getGlobal().info("超时..........");
					continue;
				}
				Logger.getGlobal().info("处理请求...........");
				// 获取处理的SelectionKey
				Iterator<SelectionKey> iter = sel.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					new Thread(new HttpHandler(key)).run();
					iter.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
