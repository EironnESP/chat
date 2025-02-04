package chat;

import servidor.ServidorTCP;
import servidor.ServidorUDP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

public class ChatUDP extends Thread{
    @Override
    public void run() {
        try {
            Thread.sleep(1000);

            ConexionUDP c = new ConexionUDP();
            //INTERFAZ
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JButton botonNombre = new JButton("Guardar nombre");
            botonNombre.setBounds(230, 10, 150, 30);

            JTextField textNombre = new JTextField();
            textNombre.setBounds(10, 10, 200, 30);

            JTextArea mensajes = new JTextArea();
            mensajes.setBounds(10, 50, 250, 350);
            mensajes.setEnabled(false);

            JButton botonEnviar = new JButton("Enviar");
            botonEnviar.setBounds(230, 410, 150, 30);
            botonEnviar.setEnabled(false);

            JTextField textMensaje = new JTextField();
            textMensaje.setBounds(10, 410, 200, 30);
            textMensaje.setEnabled(false);

            JTextArea listaUsuarios = new JTextArea("Usuarios\n");
            listaUsuarios.setBounds(270, 50, 110, 350);
            listaUsuarios.setEnabled(false);

            botonNombre.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (!textNombre.getText().contains(" ") && textNombre.getText().charAt(0) != '[' && textNombre.getText().charAt(0) != '!') {
                            c.nombre = textNombre.getText().trim();
                            if (c.conectar()) {
                                botonNombre.setEnabled(false);
                                textNombre.setEnabled(false);
                                botonEnviar.setEnabled(true);
                                textMensaje.setEnabled(true);

                                mensajes.setText(c.registroYUsuarios[0]);
                                listaUsuarios.setText(c.registroYUsuarios[1]);

                                Thread.sleep(25);
                                try {
                                    c.escribir("! " + textNombre.getText() + " se ha unido");
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    } catch (RuntimeException | IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            botonEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        c.escribir("[" + textNombre.getText() + "] " + textMensaje.getText());
                        textMensaje.setText("");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            f.add(botonNombre);
            f.add(textNombre);
            f.add(mensajes);
            f.add(botonEnviar);
            f.add(textMensaje);
            f.add(listaUsuarios);

            f.setSize(400, 500);
            f.setLayout(null);
            f.setVisible(true);

            while (!c.online);

            Thread.sleep(50);

            while (true) {
                if (c.socket != null) {
                    String mensaje = c.leer();
                    mensajes.setText(mensajes.getText() + "\n" + mensaje);

                    if (!mensaje.isEmpty() && mensaje.charAt(0) == '!') {
                        String[] partido = mensaje.split(" ");

                        if (!partido[1].equals(textNombre.getText())) {
                            listaUsuarios.setText(listaUsuarios.getText() + "\n" + partido[1]);
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
        }
    }

    public static void main(String[] args) {
        ChatUDP chat = new ChatUDP();
        chat.start();
    }


}

class ConexionUDP {
    DatagramSocket socket = null;
    InetAddress ip = InetAddress.getByName("localhost");
    String nombre;
    boolean online = false;
    String[] registroYUsuarios;

    ConexionUDP() throws IOException {
    }

    public boolean conectar() throws IOException {
        socket = new DatagramSocket();
        byte[] cadena = nombre.getBytes();
        DatagramPacket mensaje = new DatagramPacket(cadena,cadena.length, ip, 3334);
        socket.send(mensaje);

        byte[] cadena2 = new byte[1000];
        DatagramPacket respuesta = new DatagramPacket(cadena2,cadena2.length);
        socket.receive(respuesta);

        String mensaje2 = new String(respuesta.getData(), 0, respuesta.getLength());
        if (!mensaje2.equals("400")) {
            registroYUsuarios = mensaje2.split("¬");
            online = true;
            return true;
        } else {
            socket.close();
            return false;
        }
    }

    public void escribir(String s) throws IOException {
        byte[] cadena = s.getBytes();
        DatagramPacket mensaje = new DatagramPacket(cadena,cadena.length, ip, 3334);
        socket.send(mensaje);
    }

    public String leer() throws IOException {
        byte[] cadena = new byte[1000];
        DatagramPacket respuesta = new DatagramPacket(cadena,cadena.length);
        socket.receive(respuesta);

        String mensaje = new String(respuesta.getData(), 0, respuesta.getLength());
        return mensaje;
    }
}