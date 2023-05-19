import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    // private ServeWebRequest s;
    private int count;

    public ClientHandler(Socket clientSocket, int count) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        this.count = count;
        // this.s = web;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connecton " + count + " opened. (" + new Date() + ")");
            // s.serve(client, count);

            while(true) {
                String req = in.readLine();
                if(req.equals("name")) out.println("Hii");
                else out.println("Invalid request!");
            }
        } catch (IOException e) {
            System.err.println("IO exception in client handler");
            System.err.println(e.getMessage());
        } finally {  
                System.out.println("[SERVER] Connected to closing!");
                out.close();
                try {
                    in.close();
                }
                catch (IOException e) {
                    System.err.println("IO exception in client handler");
                    System.err.println(e.getMessage());
                }
    }
    
}

}