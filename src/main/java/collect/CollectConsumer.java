package collect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 收集函数结果
 *
 * @param <T>
 */
public class CollectConsumer<T> implements Consumer<T> {

    /**
     * 定时任务线程池
     */
    private ScheduledExecutorService threadPool;

    /**
     * 收集函数结果
     * 毫秒
     */
    private long delayTime;

    /**
     * 最大去收集时间
     * 超过此时间则立即执行一次业务
     * 毫秒
     */
    private long maxTime;

    /**
     * 收集开始时间
     * = null 时 则代表此次收集已结束或者未开始
     */
    private volatile Long startTime;

    /**
     * 当前业务任务
     */
    private CollectRunnable<List<T>> currentRunnable = null;

    /**
     * 前一个业务任务
     */
    private CollectRunnable<List<T>> prevRunnable = null;

    /**
     * 业务函数
     */
    private CollectBusinessConsumer<List<T>> collectBusinessConsumer;

    /**
     * 收集到的参数
     */
    private List<T> paramsList;

    public CollectConsumer(ScheduledExecutorService threadPool, CollectBusinessConsumer<List<T>> collectBusinessConsumer, long delayTime, TimeUnit delayUnit, long maxTime, TimeUnit maxTimeUnit) {
        this.threadPool = threadPool;
        this.collectBusinessConsumer = collectBusinessConsumer;
        this.delayTime = TimeUnit.MILLISECONDS.convert(delayTime, delayUnit);
        this.maxTime = TimeUnit.MILLISECONDS.convert(maxTime, maxTimeUnit);
        this.paramsList = new ArrayList<>();
    }

    @Override
    public synchronized void accept(T params) {
        // 添加参数到列表
        paramsList.add(params);

        // 如果前一个任务还未执行 则取消任务
        if (prevRunnable != null) {
            prevRunnable.setIgnore(true);
        }

        // 创建一个任务
        currentRunnable = new CollectRunnable<List<T>>(this, paramsList) {
            @Override
            void handler(List<T> paramList) {
                collectBusinessConsumer.accept(paramList);
            }
        };

        // 把任务添加进定时任务
        threadPool.schedule(currentRunnable, delayTime, TimeUnit.MILLISECONDS);
        // 当前任务变为前一个任务
        prevRunnable = currentRunnable;

    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ScheduledExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public CollectRunnable<List<T>> getCurrentRunnable() {
        return currentRunnable;
    }

    public void setCurrentRunnable(CollectRunnable<List<T>> currentRunnable) {
        this.currentRunnable = currentRunnable;
    }

    public CollectRunnable<List<T>> getPrevRunnable() {
        return prevRunnable;
    }

    public void setPrevRunnable(CollectRunnable<List<T>> prevRunnable) {
        this.prevRunnable = prevRunnable;
    }

    public CollectBusinessConsumer<List<T>> getCollectBusinessConsumer() {
        return collectBusinessConsumer;
    }

    public void setCollectBusinessConsumer(CollectBusinessConsumer<List<T>> collectBusinessConsumer) {
        this.collectBusinessConsumer = collectBusinessConsumer;
    }

    public List<T> getParamsList() {
        return paramsList;
    }

    public void setParamsList(List<T> paramsList) {
        this.paramsList = paramsList;
    }
}
