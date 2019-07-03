package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingExecutorServiceServerStarter {
    public static void main(String[] args) {
        new Thread(new BlockingExecutorServiceServer(8888)).start();
    }

}

class ClientStarter {
    public static void main(String[] args) {
        for (int i = 1; i <= 2; i++) {
            new Thread(new Clinet()).start();
        }
    }
}

class BlockingExecutorServiceServer implements Runnable {

    private final int MAX_POOL_SIZE = 2;

    private int port;

    BlockingExecutorServiceServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server is running on port: " + port);

            ExecutorService service = Executors.newFixedThreadPool(MAX_POOL_SIZE);
            while (true) {
                Socket socket = serverSocket.accept();
                service.submit(new HandlerNewSocket(socket));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static class HandlerNewSocket implements Runnable {
        final Socket socket;

        HandlerNewSocket(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                System.out.println("HandlerNewSocket new client in new socket");

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("waiting data");
                String mess = reader.readLine();
                System.out.println("收到客户端的消息：" + mess);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("已经收到你的消息\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


class Clinet implements Runnable {

    @Override
    public void run() {

        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            System.out.println("connected server");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("send data");
            writer.write("hello server!\n");
            writer.flush();
            System.out.println("flush data");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("wait server data");
            System.out.println("message from server: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
