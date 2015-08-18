package learn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.sun.mail.imap.Utility.Condition;

public class Server {
	private Selector selector = null;
	private Charset charset = Charset.forName("UTF-8");

	private ArrayList<SendToKindle> sendToKindleList = new ArrayList<SendToKindle>();
	private final Lock lock = new ReentrantLock();
	private final Condition cond = (Condition) lock.newCondition();

	public void init() throws IOException {
		ConsumerThread consumerThread = new ConsumerThread();	// 消费者线程
		consumerThread.run();
		
		selector = Selector.open();
		ServerSocketChannel server = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 10010);
		server.socket().bind(isa);
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);

		while (selector.select() > 0) {
			for (SelectionKey sk : selector.selectedKeys()) {
				selector.selectedKeys().remove(sk);

				if (sk.isAcceptable()) {
					SocketChannel client = server.accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					sk.interestOps(SelectionKey.OP_ACCEPT);
				}

				if (sk.isReadable()) {
					SocketChannel sc = (SocketChannel) sk.channel();
					ByteBuffer buff = ByteBuffer.allocate(1024);
					String content = "";

					try {
						while (sc.read(buff) > 0) {
							buff.flip();
							content += charset.decode(buff);
						}
						System.out.println(content);
						Gson gson = new Gson();
						User user = gson.fromJson(content, User.class);
						System.out.println(user.getEmail());
						System.out.println(user.getBookName());
						System.out.println(user.getBookNum());
						SendToKindle sendToKindle = new SendToKindle(user.getEmail(), user.getBookName(),
								user.getBookNum());
						sendToKindleList.add(sendToKindle);
						cond.notify();

						sk.interestOps(SelectionKey.OP_READ);
					} catch (IOException e) {
						sk.cancel();
						if (sk.channel() != null) {
							sk.channel().close();
						}
					}

					if (content.length() > 0) {
						for (SelectionKey key : selector.keys()) {
							Channel targetChannel = key.channel();
							if (targetChannel instanceof SocketChannel) {
								SocketChannel dest = (SocketChannel) targetChannel;
								dest.write(charset.encode(content));
							}
						}
					}
				}
			}
		}
	}

	private class ConsumerThread extends Thread {
		public void run() {
			while (true) {
				try {
					if (sendToKindleList.isEmpty()) {
						cond.wait();
					}
					for (SendToKindle sendToKindle : sendToKindleList) {
						sendToKindle.run();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
