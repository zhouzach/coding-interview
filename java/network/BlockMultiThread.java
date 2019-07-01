package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockMultiThread {
}

class Server implements Runnable {

    private int port;

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            while (!Thread.interrupted())
                new Thread(new Handler(ss.accept())).start();
            // or, single-threaded, or a thread pool
        } catch (IOException ex) { }
    }
}

class Handler implements Runnable {
    final Socket socket;
    private final int MAX_INPUT = 65535;

    Handler(Socket s) { socket = s; }
    public void run() {
        try {
            byte[] input = new byte[MAX_INPUT];
            socket.getInputStream().read(input);
            byte[] output = process(input);
            socket.getOutputStream().write(output);
        } catch (IOException ex) { }
    }
    private byte[] process(byte[] cmd) {
        return new byte[]{};
    }
}
