package collect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务任务抽象类
 *
 * @param <T>
 */
public abstract class CollectRunnable<T> implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 收集函数
     */
    private final CollectConsumer collectConsumer;

    /**
     * 参数
     */
    private T params;

    /**
     * 是否已运行
     */
    private volatile boolean ran = false;

    /**
     * 是否忽略当前任务
     */
    private volatile boolean ignore = false;


    public CollectRunnable(CollectConsumer collectConsumer, T params) {
        this.collectConsumer = collectConsumer;
        this.params = params;
    }

    @Override
    public void run() {
        synchronized (collectConsumer) {
            long currentTimeMillis = System.currentTimeMillis();
            Long startTime = collectConsumer.getStartTime();
            if (startTime == null) {
                startTime = currentTimeMillis;
                collectConsumer.setStartTime(startTime);
            }

            // 如果当前任务需要忽略
            if (ignore) {
                long maxTime = collectConsumer.getMaxTime();
                if (currentTimeMillis - startTime < maxTime) {
                    // 当收集时间开始与当前时间的差 小于 最大限度时间 则忽略当前
                    // 反之 如果收集时间与当前时间的差 大于最大限度时间 则执行业务逻辑
                    return;
                }
            }

            try {
                // 执行业务逻辑
                handler(params);
            } catch (Exception e) {
                logger.error("handler error", e);
            }

            // 收集完毕 收集时间置为空
            collectConsumer.setStartTime(null);

            // 清空收集参数列表
            collectConsumer.getParamsList().clear();

            // 已运行
            setRan(true);
        }
    }

    /**
     * 业务方法
     *
     * @param params 参数
     */
    abstract void handler(T params);

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public CollectConsumer getCollectConsumer() {
        return collectConsumer;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public boolean isRan() {
        return ran;
    }

    public void setRan(boolean ran) {
        this.ran = ran;
    }
}