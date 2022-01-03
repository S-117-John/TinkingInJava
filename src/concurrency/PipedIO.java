package concurrency;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务见使用管道进行输入/输出
 * PipedWriter：允许任务向管道写
 * PipedReader：允许不同任务从同一个管道中读取
 * 管道基本上是一个阻塞队列
 */
public class PipedIO {

    public static void main(String[] args) throws IOException, InterruptedException {

        Sender sender = new Sender();

        Receiver receiver = new Receiver(sender);

        ExecutorService exec = Executors.newCachedThreadPool();

        exec.execute(sender);

        exec.execute(receiver);

        TimeUnit.SECONDS.sleep(4);

        /**
         * PipedReader与普通I/O之间最重要的差异：PipedReader时可中断的
         */
        exec.shutdownNow();
    }
}

/**
 * 把数据放进Writer，然后休眠一段时间
 */
class Sender implements Runnable {

    private Random rand = new Random(47);

    private PipedWriter out = new PipedWriter();

    public PipedWriter getPipedWriter() {
        return out;
    }

    @Override
    public void run() {
        try {
            while (true){
                for (char c = 'A';c <= 'z';c++){
                    out.write(c);
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(500));
                }
            }
        } catch (IOException e) {
            System.out.println(e + " Sender write exception");
        } catch (InterruptedException e) {
            System.out.println(e + " Sender sleep interrupted");
        }
    }
}

/**
 * PipedReader的建立必须在构造器中与一个PipedWriter相关联。
 * 没有sleep()和wait().
 * 当调用read()时，如果没有更多数据，管道将自动阻塞
 */
class Receiver implements Runnable {

    private PipedReader in;

    public Receiver(Sender sender) throws IOException {
        this.in = new PipedReader(sender.getPipedWriter());
    }

    @Override
    public void run() {

        try {
            while (true){
                /**
                 * 如果将in.read()改为System.in.read()，那么interrupt()将不能打断read()调用
                 */
                System.out.println("Read: " + (char)in.read() + ",");
            }
        } catch (IOException e) {
            System.out.println(e + "Receiver read exception");
        }

    }
}



