package chat;

import servidor.ServidorTCP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ChatUDP extends Thread{
    private static int i = 0;

    public static int getNumero() {
        int n = i;
        i++;
        return n;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            int id = ChatUDP.getNumero(); // TCP

            DatagramSocket sSocket = new DatagramSocket();
            InetAddress equipo = InetAddress.getByName("localhost");

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
                    if (!ServidorTCP.usuarios.containsValue(textNombre.getText())) {
                        ServidorTCP.usuarios.put(id, textNombre.getText().trim());
                        botonNombre.setEnabled(false);
                        textNombre.setEnabled(false);
                        botonEnviar.setEnabled(true);
                        textMensaje.setEnabled(true);

                        String s = "! "+ServidorTCP.usuarios.get(id) + " se ha unido";
                        byte[] cadena = s.getBytes();
                        DatagramPacket mensaje = new DatagramPacket(cadena, cadena.length, equipo, 3334);

                        try {
                            sSocket.send(mensaje);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });

            botonEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String s = "[" + ServidorTCP.usuarios.get(id)+"] " + textMensaje.getText();
                    byte[] cadena = s.getBytes();
                    DatagramPacket mensaje = new DatagramPacket(cadena, cadena.length, equipo, 3334);

                    try {
                        sSocket.send(mensaje);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    textMensaje.setText("");
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

            while (true) {
                byte[] cadenaRespuesta = new byte[1000];

                DatagramPacket respuesta = new DatagramPacket(cadenaRespuesta, cadenaRespuesta.length);
                sSocket.receive(respuesta);
                String mensaje = new String(respuesta.getData(), 0, respuesta.getLength());

                mensajes.setText(mensajes.getText() + "\n" + mensaje);
                if (mensaje.charAt(0) == '!') {
                    mensaje = mensaje.replaceFirst("!", "").trim();
                    listaUsuarios.setText(listaUsuarios.getText() + "\n" + mensaje);
                }
            }
            
        } catch (IOException | InterruptedException e) {
        }
    }


}
