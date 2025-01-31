package servidor;

import java.io.*;
import java.net.Socket;

public class HiloServidorTCP extends Thread{
    Socket s;

    public HiloServidorTCP(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            InputStream is = s.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            while (true) {
                String str = dis.readUTF();
                System.out.println(str); //debug
                    for (Socket s1 : ServidorTCP.clientes) {
                        OutputStream os = s1.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);

                        dos.writeUTF(str);
                    }
            }
        } catch (IOException e) {}

    }
}
