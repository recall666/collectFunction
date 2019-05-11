import collect.CollectConsumer;
import collect.CollectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 收集函数
 *
 * @author recall
 * @date 2019/5/10
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CollectConsumer<Object> debounce = CollectUtil.collect(objects -> logger.info("paramList:{}", objects),
                1, TimeUnit.SECONDS,
                2, TimeUnit.SECONDS
        );
        AtomicLong index = new AtomicLong();
        Runnable testRunnable = () -> {
            while (true) {
                debounce.accept(String.format("%d", index.incrementAndGet()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("*");
            }
        };

        new Thread(testRunnable).start();
        new Thread(testRunnable).start();
        new Thread(testRunnable).start();
        new Thread(testRunnable).start();
    }

}
