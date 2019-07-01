package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockSingleServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            System.out.println("server listening on port 8888....");
            //Server is blocked, waiting for client connecting
            Socket socket = serverSocket.accept();
            System.out.println("客户端:" + socket.getInetAddress().getLocalHost() + "已连接到服务器");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //读取客户端发送来的消息
            String mess = br.readLine();
            System.out.println("收到客户端的消息：" + mess);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("server 已经你收到的消息" + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("server shut down.");
    }
}


class SingleClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 8888);

            //构建IO
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            //向服务器端发送一条消息
            bw.write("hello server\n");
            bw.flush();

            //读取服务器返回的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String mess = br.readLine();
            System.out.println("收到服务器的消息：" + mess);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("client shut down.");
    }
}