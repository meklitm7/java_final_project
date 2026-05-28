package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TesfaRMIServerMain {
    private static final int RMI_PORT = 1099; // Default RMI port

    public static void main(String[] args) {
        try {
            // Create an instance of the RMI server
            TesfaRMIServer rmiServer = new TesfaRMIServer();

            // Export the remote object
            TesfaRMI stub = (TesfaRMI) UnicastRemoteObject.exportObject(rmiServer, 0);

            // Create or get the RMI registry
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(RMI_PORT);
            } catch (Exception e) {
                System.out.println("Creating new RMI registry on port " + RMI_PORT);
                registry = LocateRegistry.createRegistry(RMI_PORT);
            }

            // Bind the remote object to the registry
            registry.rebind("TesfaRMI", stub);

            System.out.println("Tesfa RMI Server ready on port " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("Tesfa RMI Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}