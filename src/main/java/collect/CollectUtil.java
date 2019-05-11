package collect;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 收集函数工具类
 *
 * @author recall
 * @date 2019/5/9
 */
public class CollectUtil {

    /**
     * 公用线程池
     */
    private static ScheduledExecutorService commonThreadPool = Executors.newScheduledThreadPool(20);

    /**
     * 收集函数
     * 在收集时间内 重复调用收集结构函数 不会里面执行业务
     * 如果达到了最大收集时间 则立即执行一次业务
     *
     * @param business    业务
     * @param delayTime   收集时间
     * @param delayUnit   收集时间单位
     * @param maxTime     最大收集时间（超过此事件会一定会执行一次业务）
     * @param maxTimeUnit 最大收集时间单位
     * @param <T>         业务参数泛型
     * @return 收集结果函数
     */
    public static <T> CollectConsumer<T> collect(CollectBusinessConsumer<List<T>> business, long delayTime, TimeUnit delayUnit, long maxTime, TimeUnit maxTimeUnit) {
        return new CollectConsumer<>(commonThreadPool, business, delayTime, delayUnit, maxTime, maxTimeUnit);
    }



}
