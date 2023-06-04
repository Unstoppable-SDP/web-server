import java.net.Socket;


 /**
 * @(#)RequestInfo.java
 *
 * @authors
 * by Marwah Bakoor
 * @version 1.00 2023/6/4
 * This class represents information about a request received 
 * from a client through a network socket.
 * It encapsulates the socket object and the request number.
 */


public class RequestInfo {
    private Socket socket;
    private int queueCount;

    public RequestInfo( Socket socket, int queueCount) {
        this.socket = socket;
        this.queueCount = queueCount;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getQueueCount() {
        return queueCount;
    }
}

