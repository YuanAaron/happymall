package cn.coderap.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用于监控每个service执行时间的切面
 */
@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger logger=LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP的通知类型：
     * 前置通知：在方法调用之前执行；
     * 后置通知：在方法正常调用之后执行；
     * 环绕通知：在方法调用前后，分别执行一次
     * 异常通知：如果在方法调用过程中发生异常，则执行该通知
     * 最终通知：在方法调用之后执行（相当于finally）
     */

    /**
     * 切面表达式：
     * execution：所要执行的表达式主体
     * 第一处：*表示方法返回值类型（*代表所有类型）
     * 第二处：包名表示aop监控的类所在的包
     * 第三处：..表示该包及其子包
     * 第四处：*表示类名（*表示所有类）
     * 第五处：*(..)，其中*表示方法名，(..)表示方法中可以是任何参数
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* cn.coderap.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("====== 开始执行 {}.{} ======",joinPoint.getTarget().getClass(),joinPoint.getSignature().getName());
        long begin= System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long end= System.currentTimeMillis();
        long takeTime=end-begin;
        if (takeTime>3000) {
            logger.error("====== 执行结束，耗时：{} 毫秒 ======",takeTime);
        } else if (takeTime>2000) {
            logger.warn("====== 执行结束：{} 毫秒 ======",takeTime);
        } else {
            logger.info("====== 执行结束：{} 毫秒 ======",takeTime);
        }
        return res;
    }
}
