package com.yyx.aio.config.aspect;

import com.yyx.aio.entity.OperationLog;
import com.yyx.aio.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yangkai
 * @create 2017-08-21-上午 10:20
 */
@Aspect
public class ControllerLogAspect {
    @Autowired(required = false)
    private OperationLogService operationLogService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<?,?> inputParamMap = null ; // 传入参数
    private Map<String, Object> outputParamMap = null; // 存放输出结果
    private long startTimeMillis = 0; // 开始时间
    private long endTimeMillis = 0; // 结束时间

    /**
     *
     * @Title：doBeforeInServiceLayer
     * @Description: 方法调用前触发
     *  记录开始时间
     * @author yangkai
     * @date 2017年8月21日11:04:41
     * @param joinPoint
     */
    @Before("execution(* com.yyx.aio.controller..*.*(..))")
    public void doBeforeInServiceLayer(JoinPoint joinPoint) {
        startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
    }

    /**
     *
     * @Title：doAfterInServiceLayer
     * @Description: 方法调用后触发
     *  记录结束时间
     * @author yangkai
     * @date 2017年8月21日11:04:49
     * @param joinPoint
     */
    @After("execution(* com.yyx.aio.controller..*.*(..))")
    public void doAfterInServiceLayer(JoinPoint joinPoint) {
        endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
        this.printOptLog();
    }

    /**
     *
     * @Title：doAround
     * @Description: 环绕触发
     * @author yangkai
     * @date 2017年8月21日11:04:54
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.yyx.aio.controller..*.*(..))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        outputParamMap = new HashMap<String, Object>();
        Object result = pjp.proceed();// result的值就是被拦截方法的返回值
        outputParamMap.put("result", result);

        return result;
    }

    /**
     *
     * @Title：printOptLog
     * @Description: 输出日志 + 操作日志保存
     * @author yangkai
     * @date 2014年11月2日 下午4:47:09
     */
    private void printOptLog() {
        /**
         * 1.获取request信息
         * 2.根据request获取session
         * 3.从session中取出登录用户信息
         */
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)ra;
        HttpServletRequest request = sra.getRequest();
        inputParamMap = request.getParameterMap();
        OperationLog operationLog = new OperationLog();
        operationLog.setRequestMillis(endTimeMillis - startTimeMillis);
        operationLog.setOperTime(new Date(startTimeMillis));
        operationLog.setRequestParam(inputParamMap.toString().length()> 10000 ? "值过长":inputParamMap.toString());
        operationLog.setRequestResult(outputParamMap.toString().length()> 10000 ? "值过长":outputParamMap.toString());
        operationLog.setRequestUri(request.getRequestURI());
        // 从session中获取用户信息
       /* User user = UserUtil.getCurrrentUser();
        if(StringUtil.isNotNull(user)){
            operationLog.setUserId(UserUtil.getUserId());
        }else{

        }
        logger.info("\n user："+UserUtil.getLoginName()
                +"  url："+request.getRequestURI()+"; op_time：" + new Date(startTimeMillis) + " pro_time：" + (endTimeMillis - startTimeMillis) + "ms ;"
                +" param："+inputParamMap.toString()+";"+"\n result："+outputParamMap.toString());*/
        operationLogService.insertSelective(operationLog);
    }
}
