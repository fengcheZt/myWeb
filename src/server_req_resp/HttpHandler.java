package server_req_resp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class HttpHandler implements Runnable {
	private int bufferSize = 1024;
	private String localCharset = "UTF-8";
	private SelectionKey key;

	public HttpHandler(SelectionKey key) {
		this.key = key;
	}

	public void handleAccept() throws IOException {
		SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
		channel.configureBlocking(false);
		channel.register(key.selector(), SelectionKey.OP_READ,
				ByteBuffer.allocate(bufferSize));
	}

	public void handleRead() throws IOException {
		try (SocketChannel channel = (SocketChannel) key.channel();) {
			ByteBuffer buffer = (ByteBuffer) key.attachment();
			buffer.clear();
			if (-1 == channel.read(buffer)) {
				channel.close();
			} else {
				buffer.flip();
				String rcvString = Charset.forName(localCharset).newDecoder()
						.decode(buffer).toString();
				String[] requestMsg = rcvString.split("\r\n");
				for (String s : requestMsg) {
					System.out.println(s);
					if (s.isEmpty()) {
						break;
					}
				}
				// 打印首行信息
				String[] ss = requestMsg[0].split(" ");
				System.out.println();
				System.out.println("Method:\t" + ss[0]);
				System.out.println("url:\t" + ss[1]);
				System.out.println("HTTP Version:\t" + ss[2]);
				System.out.println();

				// 返回客户端
				StringBuilder sb = new StringBuilder();
				sb.append("HTTP/1.1 200 OK\r\n");
				sb.append("Content-Type:text/html;charset=")
						.append(localCharset).append("\r\n");
				sb.append("\r\n");
				sb.append("<html><head><title>the title报文</title></head>");
				sb.append("<body>");
				sb.append("获得的报文信息为").append("<br/>");
				for (String s : requestMsg) {
					sb.append(s).append("<br/>");
				}
				sb.append("</body>");
				sb.append("</html>");
				buffer = ByteBuffer.wrap(sb.toString().getBytes(localCharset));
				channel.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			if (key.isAcceptable()) {
				handleAccept();
			}
			if (key.isReadable()) {
				handleRead();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
