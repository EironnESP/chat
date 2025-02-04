package servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ServidorUDP {
    public static TreeMap<Integer, String> usuarios = new TreeMap<>();
    public static List<Socket> clientes = new ArrayList<>();
    public static String registroMensajes = "BIENVENIDO AL CHAT";
    private static int i = 0;

    public static void main(String[] args) {
        DatagramSocket sSocket;
        try {
            sSocket = new DatagramSocket(3334);
            while(true) {
                byte[] cadena = new byte[1000];
                DatagramPacket mensaje = new DatagramPacket(cadena, cadena.length);
                sSocket.receive(mensaje);
                String datos = new String(mensaje.getData(), 0, mensaje.getLength());

                String s = "Respuesta";
                byte[] cadena2 = s.getBytes();
                DatagramPacket respuesta = new DatagramPacket(cadena2, cadena2.length, mensaje.getAddress(), mensaje.getPort());
                sSocket.send(respuesta);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
