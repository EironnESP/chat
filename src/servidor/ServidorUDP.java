package servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ServidorUDP {
    public static List<DatagramPacket> clientes = new ArrayList<>();
    public static TreeMap<String, Integer> usuariosPuertos = new TreeMap<>();
    public static String registroMensajes = "BIENVENIDO AL CHAT";

    public static void main(String[] args) {
        DatagramSocket sSocket;
        try {
            sSocket = new DatagramSocket(3334);
            while(true) {
                byte[] cadena = new byte[1000];
                DatagramPacket mensaje = new DatagramPacket(cadena, cadena.length);
                sSocket.receive(mensaje);

                String datos = new String(mensaje.getData(), 0, mensaje.getLength());

                if (datos.charAt(0) == '[' || datos.charAt(0) == '!') {
                    //USUARIO YA ACTIVO

                    ServidorUDP.registroMensajes += "\n" + datos;

                    for (DatagramPacket s1 : ServidorUDP.clientes) {
                        byte[] mensajeEnviado = datos.getBytes();
                        DatagramPacket envio = new DatagramPacket(mensajeEnviado, mensajeEnviado.length, s1.getAddress(), s1.getPort());
                        sSocket.send(envio);
                    }

                } else if (ServidorUDP.usuariosPuertos.containsKey(datos)) {
                    //USUARIO ERRONEO

                    String str = "400";
                    byte[] mensajeEnviado = str.getBytes();
                    DatagramPacket envio = new DatagramPacket(mensajeEnviado, mensajeEnviado.length, mensaje.getAddress(), mensaje.getPort());
                    sSocket.send(envio);

                } else {
                    //USUARIO NUEVO

                    usuariosPuertos.put(datos, mensaje.getPort());
                    ServidorUDP.clientes.add(mensaje);
                    String str = ServidorUDP.registroMensajes + "¬" + "Usuarios";

                    for (String s : ServidorUDP.usuariosPuertos.keySet()) {
                        str += "\n" + s;
                    }

                    byte[] mensajeEnviado = str.getBytes();
                    DatagramPacket envio = new DatagramPacket(mensajeEnviado, mensajeEnviado.length, mensaje.getAddress(), mensaje.getPort());
                    sSocket.send(envio);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
