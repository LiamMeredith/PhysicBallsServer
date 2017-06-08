/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.physicballs.items.Peticion;

/**
 *
 * @author Liam-Portatil
 */
public class ClientThread extends Thread {

    /**
     * Global parameters
     */
    protected Socket clientSock;
    protected String cliAddr;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    protected boolean live = true;
    protected Peticion peticion;

    /**
     * Constructor
     * @param s
     * @param cliAddr 
     */
    public ClientThread(Socket s, String cliAddr) {
        clientSock = s;
        this.cliAddr = cliAddr;
        System.out.println("Client connection from " + cliAddr);
    }

    /**
     * Common method for all of the clients thread that closes the thread life
     */
    public void terminate() {
        this.live = false;
    }
}
