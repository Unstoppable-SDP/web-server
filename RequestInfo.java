import java.net.Socket;

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