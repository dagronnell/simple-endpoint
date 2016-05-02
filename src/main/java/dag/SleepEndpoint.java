package dag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SleepEndpoint {

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(1000);
        try {

            ServerSocket ss = new ServerSocket(8080);

            int cnt = 0;
            for (;;) {
                Socket socket = ss.accept();
                pool.execute(() -> {
                    handle(socket);
                });
                System.out.println(cnt++);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            String delay = null;
            while ((line = reader.readLine()).length() > 0) {
                if (line.startsWith("GET")) {
                    int start = line.indexOf("delay=");
                    if (start > -1) {
                        int end = line.indexOf(" ", start);
                        delay = line.substring(start + 6, end);
                    }
                }
            }

            if (delay != null) {
                Thread.sleep(Integer.valueOf(delay));
            }

            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            writer.println("HTTP-Version: HTTP/1.0 200 OK");
            writer.println("Content-Length: 2");
            writer.println("Content-Type: text/html");
            writer.println();
            writer.println("Ok");
            writer.println();
            writer.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
