package chat;

import servidor.ServidorTCP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatTCP extends Thread {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            int id = ServidorTCP.getNumero();
            Socket cliente = new Socket("localhost", 3333);

            OutputStream os = cliente.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            InputStream is = cliente.getInputStream();
            DataInputStream dis = new DataInputStream(is);

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
                        try {
                            dos.writeUTF("! "+ServidorTCP.usuarios.get(id) + " se ha unido");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });

            botonEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        dos.writeUTF("[" + ServidorTCP.usuarios.get(id)+"] " + textMensaje.getText());
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

            while (true) {
                String mensaje = dis.readUTF();
                mensajes.setText(mensajes.getText() + "\n" + mensaje);
                if (mensaje.charAt(0) == '!') {
                    String[] partido = mensaje.split(" ");
                    listaUsuarios.setText(listaUsuarios.getText() + "\n" + partido[1]);
                }
            }

            //dos.close();
            //os.close();
            //cliente.close();
        } catch (IOException | InterruptedException e) {
        }
    }


}
