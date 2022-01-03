package concurrency;

/**
 * 哲学家就餐问题
 * 筷子和哲学家数量相同。
 * 他们坐在桌子周围，每人之间放一根筷子。
 * 当一个哲学家要就餐的时候，这个哲学家必须同时得到左边和右边的筷子。
 * 如果一个哲学家左边或右边已经有人在使用筷子了，那么这个哲学家就必须等待，直至可得到必须的筷子
 */
public class Chopstick {

    private boolean taken = false;

    public synchronized void take() throws InterruptedException {
        while (taken){
            wait();
        }
        taken = true;
    }

    public synchronized void drop() {
        taken = false;
        notifyAll();
    }
}
