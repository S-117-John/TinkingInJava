package concurrency;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 一台机器有三个任务：一个制作土司、一个给土司抹黄油，另一个在抹过黄油的土司上涂果酱
 */

/**
 * Toast中没有任何显示同步，由队列和系统的设计隐式的管理，
 * 每片Toast在任何时刻都只由一个任务在操作。
 * 因为队列的阻塞，是的处理过程将被自动的挂起和恢复。
 * BlockingQueue产生的简化十分明显。
 * 在使用显示的wait()和notifyAll()时存在的类和类之间的耦合被消除了，因为每个类都只和它的BlockingQueue通信
 */
class Toast {
    public enum Status {
        DRY,
        BUTTERED,
        JAMMED
    }

    private Status status = Status.DRY;
    private final int id;

    public Toast(int id) {
        this.id = id;
    }

    public void butter() {
        status = Status.BUTTERED;
    }

    public void jam() {
        status = Status.JAMMED;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Toast{" +
                "status=" + status +
                ", id=" + id +
                '}';
    }
}

class ToastQueue extends LinkedBlockingQueue<Toast>{

}

class Toaster implements Runnable {

    private ToastQueue toastQueue;

    private int count = 0;

    private Random rand = new Random(47);

    public Toaster(ToastQueue toastQueue) {
        this.toastQueue = toastQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                TimeUnit.MILLISECONDS.sleep(100+ rand.nextInt(500));
                Toast t = new Toast(count++);
                System.out.println(t);
                toastQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Toast interrupted");
        }
        System.out.println("Toaster off");
    }
}

class Butterer implements Runnable {

    private ToastQueue dryQueue,butteredQueue;

    public Butterer(ToastQueue dryQueue, ToastQueue butteredQueue) {
        this.dryQueue = dryQueue;
        this.butteredQueue = butteredQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                Toast t = dryQueue.take();
                t.butter();
                System.out.println(t);
                butteredQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Butterer off");
        }
    }
}

class Jammer implements Runnable {

    private ToastQueue butteredQueue,finishedQueue;

    public Jammer(ToastQueue butteredQueue, ToastQueue finishedQueue) {
        this.butteredQueue = butteredQueue;
        this.finishedQueue = finishedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                Toast t = butteredQueue.take();
                t.jam();
                System.out.println(t);
                finishedQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Jammer interrupted");
        }
        System.out.println("Jammer off");
    }
}

class Eater implements Runnable {
    private ToastQueue finishedQueue;
    private int counter = 0;

    public Eater(ToastQueue finishedQueue) {
        this.finishedQueue = finishedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                Toast t = finishedQueue.take();
                if(t.getId()!=counter++ || t.getStatus() != Toast.Status.JAMMED){

                    System.out.println(">>>>>> Error: "+t);
                    System.exit(1);
                }else {
                    System.out.println("Chop! "+t);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Eater interrupted");
        }
        System.out.println("Eater off");
    }
}

public class ToastOMatic {

    public static void main(String[] args) throws InterruptedException {
        ToastQueue dtyQueue = new ToastQueue(),
                butteredQueue = new ToastQueue(),
                finishedQueue = new ToastQueue();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Toaster(dtyQueue));
        exec.execute(new Butterer(dtyQueue,butteredQueue));
        exec.execute(new Jammer(butteredQueue,finishedQueue));
        exec.execute(new Eater(finishedQueue));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}
