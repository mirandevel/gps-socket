/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servidorudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {

    final int PUERTO_SERVIDOR = 5000;
    byte[] buffer = new byte[1024];
    LinkedList<String> lista;
    final int SIZE = 10;

    Timer timer;
    boolean encendido;

    public Cliente() {
        lista = new LinkedList<>();
        encendido = true;
        timer = new Timer();

    }

    public void add(int x, int y) {
        String mensaje = String.valueOf(x) + "," + String.valueOf(y);
        if(lista.size()==SIZE) lista.removeFirst();
        lista.add(mensaje);

    }

    public void receiveUDPMessage(int port) throws
            IOException {
        byte[] buffer = new byte[1024];
        DatagramSocket socket = new DatagramSocket(4321);
        InetAddress group = InetAddress.getByName("localhost");

        while (encendido) {
            DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(),
                    packet.getOffset(), packet.getLength());
            System.out.println("recibido: " + msg);
            for (int i = 0; i < lista.size(); i++) {
                {
                    if (lista.get(i).compareTo(msg.trim()) == 0) {
                        lista.remove(i);
                    }
                }

            }
            if (!lista.isEmpty()) {
                enviar();
            }

        }
        socket.close();
    }

    public void enviar() {
        try {
            if(!lista.isEmpty()){
            DatagramSocket socket = new DatagramSocket();
            InetAddress group = InetAddress.getByName("localhost");
            byte[] msg = lista.getFirst().getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length,
                    group, PUERTO_SERVIDOR);
            socket.send(packet);
            System.out.println("enviado: " + new String(msg));
            socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    enviar();
                }
            };
            timer.schedule(task, 0, 1000);
            receiveUDPMessage(4321);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
