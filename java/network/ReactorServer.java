package network;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class ReactorServerStarter {
    public static void main(String[] args) {
        Runnable r;
        try {
            r = new ReactorServer("localhost", 8888);
        } catch (IOException ie) {
            ie.printStackTrace();
            r = null;
        }

        if (r != null) {
            new Thread(r).start();
        }


    }
}

class ReactorClientStarter {

    private static final int MAX_POOL_SIZE = 1;
    private static final int TASK_NUM = 5;

    public static void main(String[] args) {
//        for (int i = 1; i <= 1; i++) {
//            new Thread(new ReactorClient()).start();
//        }


        ExecutorService executorService = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        System.out.println("init ExecutorService");
        int i = 1;
        // 一个线程执行5个客户端的连接任务
        while (i <= TASK_NUM) {
            executorService.submit(new ReactorClient());
            i++;
        }
        executorService.shutdown();

        System.out.println("finish main");
    }

}

/**
 * http://afghl.github.io/2016/12/18/java-nio-03-nio-socket-server.html
 * http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf
 * https://examples.javacodegeeks.com/core-java/nio/java-nio-socket-example/
 */
public class ReactorServer implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;
    private InetSocketAddress listenAddress;

    ReactorServer(String address, int port) throws IOException {
//        selector = Selector.open();
//        serverSocketChannel = ServerSocketChannel.open();

//        Alternatively, use explicit SPI provider:
        SelectorProvider selectorProvider = SelectorProvider.provider();
        selector = selectorProvider.openSelector();
        serverSocketChannel = selectorProvider.openServerSocketChannel();

        listenAddress = new InetSocketAddress(address, port);
        serverSocketChannel.socket().bind(listenAddress);
        serverSocketChannel.configureBlocking(false);

        SelectionKey acceptSelectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        acceptSelectionKey.attach(new Acceptor());

        System.out.println("Server started.");
    }

    class Acceptor implements Runnable {    //inner
        Acceptor() {
            System.out.println("Acceptor construct-------------");
        }

        public void run() {
            try {
                System.out.println("Acceptor run");
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                }
                System.out.println("Connection Accepted by Reactor");
                System.out.println("Acceptor finish run");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {

                System.out.println("server is selecting");
                //performs a blocking selection operation
                selector.select();

                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) (it.next()));
                }
//                selectedKeys.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        System.out.println("dispatch start");
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            // or submit to ExecutorService
            r.run();
        }
        System.out.println("dispatch finish");
    }


}

final class Handler implements Runnable {
    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;
    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    ByteBuffer input = ByteBuffer.allocate(MAXIN);
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);
    static final int READING = 0, SENDING = 1;
    int state = READING;
//    String clientName = "";

    Handler(Selector selector, SocketChannel c) throws IOException {
        System.out.println("Handler construct******************");
        socketChannel = c;
        c.configureBlocking(false);
        selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
//        selectionKey.interestOps(SelectionKey.OP_READ);
//        selector.wakeup();
    }

    public void run() {
        System.out.println("start handle");
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("finish handle");
    }


    void read() throws IOException {
        socketChannel.read(input);
        if (inputIsComplete()) {
            process(input);
            state = SENDING;
            // Normally also do first write now
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        System.out.println("server send");
        String msg = "server receive sent messages";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("UTF-8"));
        socketChannel.write(buffer);
        if (outputIsComplete()) selectionKey.cancel();
    }

    boolean inputIsComplete() {
        return true;
    }

    boolean outputIsComplete() {
        return true;
    }

    void process(ByteBuffer byteBuffer) {
        System.out.println("message from client: " + new String(byteBuffer.array()).trim());
    }
}

class ReactorClient implements Runnable {
    private static final int MAXOUT = 1024;
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    @Override
    public void run() {

        try {
//            Socket socket = new Socket("127.0.0.1", 8888);
//            System.out.println("connected server");

//            socket.getOutputStream().write("hello server!".getBytes("UTF-8"));
//            byte[] input = new byte[MAX_INPUT];
//            socket.getInputStream().read(input);
//            process(input);

            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8888);
            SocketChannel socketChannel = SocketChannel.open(hostAddress);
            System.out.println("Client started");

            String threadName = Thread.currentThread().getName();

            String msg = "hi server, i am " + threadName;
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("UTF-8"));
            socketChannel.write(buffer);
            buffer.clear();

            socketChannel.read(output);
            System.out.println(threadName + " gets message from server: " + new String(output.array()).trim());
            output.clear();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}