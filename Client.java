import java.net.*;
import java.io.*;


 /**
 * @(#)Client.java
 *
 * @author 
 * by Marwah Bakoor 
 * @version 1.00 2023/6/2
 * This is a Client class that will be used to test the web server
 * by sending a request to the server and printing the response.
 */



public class Client {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:8085"); // Replace with your server URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("Response code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Response body: " + response.toString());
    }
}