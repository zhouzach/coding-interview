package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingMultithreadedServerStarter {

    public static void main(String[] args) {
        new Thread(new BlockingMultithreadedServer(8888)).start();

    }

}

class BlockingMultithreadedClientStarter {

    public static void main(String[] args) {

        for (int i = 1; i <= 2; i++) {
            new Thread(new BlockingMultithreadedClient()).start();
        }
    }

}

class BlockingMultithreadedServer implements Runnable {

    private int port;
    private static final int MAX_INPUT = 512;

    BlockingMultithreadedServer(int port) {
        this.port = port;
        System.out.println("server is running on port: " + port);
    }

    /**
     * Classic ServerSocket Loop
     */
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);

            while (!Thread.interrupted()) {
                new Thread(new Handler(ss.accept())).start();
                // or, single-threaded, or a thread pool
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static class Handler implements Runnable {
        final Socket socket;

        Handler(Socket s) {
            socket = s;
        }

        public void run() {
            try {

//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String mess = reader.readLine();
//                System.out.println("收到客户端的消息：" + mess);
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                writer.write("已经收到你的消息\n");
//                writer.flush();


                byte[] input = new byte[MAX_INPUT];
                socket.getInputStream().read(input);
                byte[] output = process(input);
                socket.getOutputStream().write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private byte[] process(byte[] cmd) throws IOException {
            System.out.println("messages from client: "+ new String(cmd).trim());
            return "has received your messages".getBytes("UTF-8");
        }

    }
}

class BlockingMultithreadedClient implements Runnable {
    private static final int MAX_INPUT = 512;

    @Override
    public void run() {

        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            System.out.println("connected server");

//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            System.out.println("send data");
//            writer.write("hello server!\n");
//            writer.flush();
//            System.out.println("flush data");
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            System.out.println("wait server data");
//            System.out.println("message from server: " + reader.readLine());

            socket.getOutputStream().write("hello server!".getBytes("UTF-8"));

            byte[] input = new byte[MAX_INPUT];
            socket.getInputStream().read(input);
            process(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(byte[] cmd) {
        System.out.println("messages from server: "+new String(cmd).trim());
    }
}

