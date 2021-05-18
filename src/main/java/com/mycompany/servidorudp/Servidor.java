/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servidorudp;

import com.mycompany.servidorudp.PosicionListenner;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable{
   
    
      PosicionListenner listenner;
    final int PUERTO = 5000;
    byte[] buffer = new byte[1024];
    DatagramSocket socket;
    boolean encendido;

   public  void sendUDPMessage(String mensaje,int port) throws IOException {
           
      DatagramSocket socket = new DatagramSocket();
      InetAddress group = InetAddress.getByName("localhost");
      byte[] msg = mensaje.getBytes();
      DatagramPacket packet = new DatagramPacket(msg, msg.length,
         group, port);
      socket.send(packet);
      socket.close();
   }
   
       public  void receiveUDPMessage(int port) throws
            IOException {
        byte[] buffer = new byte[1024];
        DatagramSocket socket = new DatagramSocket(PUERTO);
        InetAddress group = InetAddress.getByName("localhost");

        while (encendido) {
            DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(),
                    packet.getOffset(), packet.getLength());
            String[] coordenadas = msg.split(",");
            int tmpx = Integer.parseInt(coordenadas[0].trim());
            int tmpy = Integer.parseInt(coordenadas[1].trim());
            listenner.posicion(tmpx, tmpy);
            sendUDPMessage(msg,4321);
            //sendUDPMessage(msg,packet.getPort());
            
        }
        socket.close();
    }
public Servidor(PosicionListenner listenner) {
        this.listenner = listenner;
        encendido=true;
        System.out.println("Iniciando el servidor...");
        
    }
    
        void apagar() {
        encendido=!encendido;
        socket.close();
        System.out.println("Apagado");
    }

    @Override
    public void run() {
        try {
        receiveUDPMessage(PUERTO);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
}