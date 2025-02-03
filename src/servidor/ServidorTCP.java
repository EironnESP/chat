package servidor;

import chat.ChatTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class ServidorTCP {
    public static TreeMap<Integer, String> usuarios = new TreeMap<>();
    public static List<Socket> clientes = new ArrayList<>();
    private static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        List<ChatTCP> chats = new ArrayList<>();
        chats.add(new ChatTCP());
        chats.add(new ChatTCP());
        chats.get(0).start();
        Thread.sleep(50);
        chats.get(1).start();

        try {
            ServerSocket ss = new ServerSocket(3333);
            while(true) {
                Socket s = ss.accept();
                clientes.add(s);
                HiloServidorTCP h = new HiloServidorTCP(s);
                h.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getNumero() {
        int n = i;
        i++;
        return n;
    }


}
