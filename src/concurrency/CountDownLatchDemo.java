package concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch
 * 同步一个或多个任务，强制它们等待由其他任务执行的一组操作完成。
 * 你可以向CountDownLatch对象设置一个初始计数值，任何再这个对象上调用wait()的方法都将阻塞，直至这个计数值到达0。
 * 其他任务在结束其工作时，可以在该对象上调用countDown()来减小这个计数值。
 * CountDownLatch被设计为只出发一次，计数值不能被重置。
 */
public class CountDownLatchDemo {

    static final int SIZE = 100;

    public static void main(String[] args) {

        ExecutorService exec = Executors.newCachedThreadPool();


        /**
         * 所有的任务都使用同一个单一的CountDownLatch
         */
        CountDownLatch latch = new CountDownLatch(SIZE);

        for (int i = 0; i < 10; i++) {

            exec.execute(new WaitingTask(latch));
        }

        for (int i = 0; i < SIZE; i++) {
            exec.execute(new TaskPortion(latch));
        }

        System.out.println("Launch all tasks");

        exec.shutdown();
    }
}

/**
 * 随机休眠一段时间，模拟这部分工作完成
 */
class TaskPortion implements Runnable {

    private static int counter = 0;

    private final int id = counter++;

    private static Random rand = new Random(47);

    private final CountDownLatch latch;

    public TaskPortion(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {

        try {
            doWork();
            latch.countDown();
        } catch (InterruptedException e) {

        }
    }

    public void doWork() throws InterruptedException {

        TimeUnit.MILLISECONDS.sleep(rand.nextInt(2000));

        System.out.println(this + "completed");
    }

    @Override
    public String toString() {
        return "TaskPortion{" +
                "id=" + id +
                '}';
    }
}

/**
 * 表示系统必须等待的部分，它要等待到问题的初始部分完成为止
 */
class WaitingTask implements Runnable {

    private static int counter = 0;

    private final int id = counter++;

    private final CountDownLatch latch;

    public WaitingTask(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {

        try {

            latch.await();

            System.out.println("Latch barrier passed for " + this);

        } catch (InterruptedException e) {

            System.out.println(this + " interrupted");

        }
    }

    @Override
    public String toString() {
        return "WaitingTask{" +
                "id=" + id +
                '}';
    }
}