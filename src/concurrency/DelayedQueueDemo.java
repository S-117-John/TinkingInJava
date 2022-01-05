package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * DelayedQueue
 * 无界的BlockingQueue，用于放置实现了Delayed接口的对象，其中的对象只能在其到期时才能从队列中取走。
 * 这种队列时有序的，即对头对象的延迟到期时间最长。
 * 如果没有任何延迟到期，那么就不会由任何头元素，并且poll()将返回null（正因为这样，你不能将null放置到这种队列中）
 */
public class DelayedQueueDemo {

    public static void main(String[] args) {

        Random rand = new Random(47);

        ExecutorService exec = Executors.newCachedThreadPool();

        DelayQueue<DelayedTask> queue = new DelayQueue<>();

        for (int i = 0; i < 20; i++) {

            queue.put(new DelayedTask(rand.nextInt(5000)));

        }

        queue.add(new DelayedTask.EndSentinel(5000,exec));

        exec.execute(new DelayedTaskConsumer(queue));
    }
}

class DelayedTask implements Runnable, Delayed {

    private static int counter = 0;

    private final int id = counter++;

    private final int delta;

    private final long trigger;

    //保存任务被创建的顺序
    protected static List<DelayedTask> sequence = new ArrayList<>();

    public DelayedTask(int delayInMilliseconds) {

        delta = delayInMilliseconds;

        trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delta,TimeUnit.MILLISECONDS);

        sequence.add(this);
    }

    @Override
    public int compareTo(Delayed o) {

        DelayedTask that = (DelayedTask) o;

        if(trigger < that.trigger) {

            return -1;

        }

        if(trigger > that.trigger) {

            return 1;

        }

        return 0;
    }

    @Override
    public void run() {

        System.out.println(this + " ");

    }

    @Override
    public String toString() {
        return "DelayedTask{" +
                "id=" + id +
                ", delta=" + delta +
                '}';
    }

    public String summary() {

        return "(" + id + ":" + delta + ")";
    }

    /**
     * 告知延迟到期有多长时间，或者延迟在多长时间之前已经到期
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {

        return unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    /**
     * 提供一种关闭所有事物的途径，具体做法时将其放置为队列的最后一个元素
     */
    public static class EndSentinel extends DelayedTask {

        private ExecutorService exec;

        public EndSentinel(int delayInMilliseconds, ExecutorService e) {

            super(delayInMilliseconds);

            exec = e;
        }

        @Override
        public void run() {

            for (DelayedTask delayedTask : sequence) {

                System.out.println(delayedTask.summary() + " ");

            }

            System.out.println();

            System.out.println(this + "Calling shutdownNow()");

            exec.shutdownNow();

        }
    }
}

/**
 * DelayedTaskConsumer自身是一个任务，所有它有自己的Thread,它可以使用这个线程来运行从队列中获取的所有任务
 */
class DelayedTaskConsumer implements Runnable {

    private DelayQueue<DelayedTask> q;

    public DelayedTaskConsumer(DelayQueue<DelayedTask> q) {
        this.q = q;
    }

    @Override
    public void run() {

        try {

            while (!Thread.interrupted()) {

                //当前线程中运行任务
                q.take().run();
            }
        } catch (InterruptedException e) {

        }

        System.out.println("Finished DelayedTaskConsumer");
    }
}
