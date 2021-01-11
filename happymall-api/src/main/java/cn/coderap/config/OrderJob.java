package cn.coderap.config;

import cn.coderap.service.OrderService;
import cn.coderap.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by yw
 * 2021/1/8
 */
@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 使用定时任务关闭超期未支付订单，存在的弊端：
     * 1、会有时间差，程序不严谨
     *      10：39下单，11：00检查发现不足1小时，12：00检查，发现超过1小时多余21分钟
     * 2、不支持集群
     *      单机没毛病，使用集群后，就会有多个定时任务
     *      解决方案：只使用一台计算机节点，单独用来运行所有的定时任务
     * 3、会对数据库全表搜索，极其影响数据库性能（没有分页）：select * from order_status where orderStatus = 10;
     *
     * 总结：定时任务只适用于小型轻量级项目、传统项目，互联网项目一般使用延时任务（队列），这个在学习MQ时会涉及到。
     * 具体：比如10：39下单（未付款），如果是定时任务，在11点进行检查，在12点才能把该订单关闭；如果使用延时队列，那么它只需要在11：39检查该订单，
     * 如果其order_status是未付款，直接关闭订单，即针对该订单后者只会查询数据库一次。//TODO 使用延时队列来关闭超期未支付订单
     */

    //@Scheduled(cron = "0/3 * * * * ?") //每隔3s，由https://cron.qqe2.com/生成
    @Scheduled(cron = "0 0 0/1 * * ?") //每隔1h
    public void autoCloseOrder() {
        //System.out.println("执行定时任务，当前时间为："+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
        orderService.closeOrder();
    }
}
