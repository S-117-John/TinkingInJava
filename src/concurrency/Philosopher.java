package concurrency;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 任何两个Philosopher都不能成功take()同一根筷子。
 * 如果一根Chopstick已经被某个Philosopher获得，
 * 那么另一个Philosopher可以wait(),直至这根Chopstick的当前持有者调用drop()使其可用为止。
 * 当一个Philosopher任务调用take()时，这个Philosopher任务将等待，
 * 直至taken标志变为false（直至当前持有Chopstick的Philosopher释放它）。
 * 然后这个任务会将taken标志设置为true，以表示现在由新的Philosopher持有这根Chopstick，
 * 它会调用drop()来修改标志的状态，
 * 并notifyAll()所有其他的Philosopher，
 * 这些Philosopher中有些可能就在wait()这跟Chopstick
 */
public class Philosopher implements Runnable {

    private Chopstick left,right;

    private final int id;

    private final int ponderFactor;

    private Random rand = new Random(47);

    private void pause() throws InterruptedException {
        if(ponderFactor == 0){
            return;
        }

        TimeUnit.MILLISECONDS.sleep(rand.nextInt(ponderFactor*250));
    }

    public Philosopher(Chopstick left, Chopstick right, int id, int ponderFactor) {
        this.left = left;
        this.right = right;
        this.id = id;
        this.ponderFactor = ponderFactor;
    }

    /**
     * 在run()中，每个Philosopher不断的思考，吃饭。
     * 如果ponderFactor不为0，
     * 则pause()方法会休眠（sleep()）一段随机时间。
     * 通过使用这种方式，你将看到Philosopher会在思考上花掉一段随机化的时间，然后尝试着获取（take()）右边和左边Chopstick，
     * 随后在吃饭上再花掉一段随机化时间，之后重复此过程
     */
    @Override
    public void run() {

        try {

            while (!Thread.interrupted()){

                System.out.println(this + " " + "thinking");

                pause();

                //Philosopher becomes hungry
                System.out.println(this + " " + "grabbing right");

                right.take();

                System.out.println(this + " " + "grabbing left");

                left.take();

                System.out.println(this + " " + "eating");

                pause();

                right.drop();

                left.drop();
            }

        } catch (InterruptedException e) {
            System.out.println(this + " " + "exiting via interrupt");
        }
    }

    @Override
    public String toString() {
        return "Philosopher{" +
                "id=" + id +
                '}';
    }
}
