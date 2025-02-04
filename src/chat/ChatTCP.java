package chat;

import servidor.ServidorTCP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatTCP extends Thread {

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

            int id = ChatTCP.getNumero();

            ConexionTCP c = new ConexionTCP();
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
                    if (!ServidorTCP.usuarios.containsValue(textNombre.getText()) && !textNombre.getText().contains(" ")) {
                        ServidorTCP.usuarios.put(id, textNombre.getText().trim());
                        botonNombre.setEnabled(false);
                        textNombre.setEnabled(false);
                        botonEnviar.setEnabled(true);
                        textMensaje.setEnabled(true);
                        try {
                            c.conectar("localhost", 3333);
                            c.dos.writeUTF("! "+ServidorTCP.usuarios.get(id) + " se ha unido");
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
                        c.dos.writeUTF("[" + ServidorTCP.usuarios.get(id)+"] " + textMensaje.getText());
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

            boolean conexionAbierta = false;

            while(!conexionAbierta) {
                if (c.dis != null) {
                    String mensajesPasados = c.dis.readUTF();
                    if (!mensajesPasados.isEmpty()) {
                        mensajes.setText(mensajes.getText() + "\n" + mensajesPasados);
                        listaUsuarios.setText(c.dis.readUTF());
                    }
                    conexionAbierta = true;
                }
            }

            while (true) {
                if (c.dis != null) {
                    String mensaje = c.dis.readUTF();
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


}

class ConexionTCP {
    Socket socket = null;

    OutputStream os;
    DataOutputStream dos;

    InputStream is;
    DataInputStream dis;

    ConexionTCP() throws IOException {
    }

    public boolean conectar(String host, int port) {
        try {
            this.socket = new Socket(host, port);

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            is = socket.getInputStream();
            dis = new DataInputStream(is);

            return true;
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            return false;
        }
    }
}
