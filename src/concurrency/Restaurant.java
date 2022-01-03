package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 生产者与消费者
 * 请考虑这样一个饭店，它有一个厨师和一个服务员。
 * 这个服务员必须等待初始准备好膳食。
 * 当厨师准备好时，他会通知服务员，之后服务员上菜，然后返回继续等待。
 * 这个时一个任务协作的示例：厨师代表生产者，而服务员代表消费者。
 * 两个任务必须在膳食被生产和消费时进行握手，而系统必须以有序的方式关闭.
 * Restaurant是WaitPerson和Chef的焦点，他们都必须知道在为哪个Restaurant工作，因为他们必须和这家饭店的“餐窗”打交道，以便放置或拿取膳食restaurant.meal。
 *
 */
public class Restaurant {

    Meal meal;
    ExecutorService exec = Executors.newCachedThreadPool();
    WaitPerson waitPerson = new WaitPerson(this);
    Chef chef = new Chef(this);

    public Restaurant() {
        exec.execute(chef);
        exec.execute(waitPerson);
    }

    public static void main(String[] args) {
        new Restaurant();
    }
}

class Meal {
    private final int orderNum;

    public Meal(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "concurrency.Meal{" +
                "orderNum=" + orderNum +
                '}';
    }
}

class WaitPerson implements Runnable {

    private Restaurant restaurant;

    public WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                synchronized (this){
                    while (restaurant.meal == null){
                        //进入wait模式，停止任务，直至被Chef的notifyAll唤醒。
                        //只有一个任务将在WaitPerson的锁上等待：即WaitPerson任务自身。
                        //理论上可以调用notify
                        wait();
                    }
                }
                System.out.println("Waitperson got "+restaurant.meal);
                synchronized (restaurant.chef){
                    restaurant.meal = null;
                    restaurant.chef.notifyAll();
                }
            }

        } catch (InterruptedException e) {
            System.out.println("concurrency.WaitPerson interrupted");
        }

    }
}

class Chef implements Runnable {
    private Restaurant restaurant;
    private int count = 0;

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                synchronized (this){
                    //Chef送上Meal并通知WaitPerson，就将等待，直至WaitPerson收集到订单并通知Chef
                    while (restaurant.meal!=null){
                        //wait（）被包装在一个while中，不断地测试正在等待的事务
                        wait();
                    }
                }
                if(++count==10){
                    System.out.println("Out of food,closing");
                    restaurant.exec.shutdownNow();
                }
                System.out.println("Order up!");
                //对notifyAll的调用必须首先捕获waitPerson上的锁，而在WaitPerson.run()中的对wait()的调用会自动释放这个锁
                synchronized (restaurant.waitPerson){
                    restaurant.meal = new Meal(count);
                    restaurant.waitPerson.notifyAll();
                }
                //当任务进入一个
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("concurrency.Chef interrupted");
        }
    }
}