package concurrency;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CloseResource {

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();

        ServerSocket server = new ServerSocket(8080);

        InputStream sockInput = new Socket("localhost",8080).getInputStream();

        exec.execute(new IOBlocked(sockInput));

        exec.execute(new IOBlocked(System.in));

        TimeUnit.MILLISECONDS.sleep(100);

        System.out.println("Shutting down all threads");

        exec.shutdownNow();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("Closing"+sockInput.getClass().getName());

        sockInput.close();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("Closing"+System.in.getClass().getName());

        System.in.close();
    }

}

