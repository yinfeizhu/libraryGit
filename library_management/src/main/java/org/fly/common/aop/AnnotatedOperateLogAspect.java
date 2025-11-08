package org.fly.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.fly.entity.OperateLog;
import org.fly.service.OperatorLogService;
import org.fly.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect // 声明这是一个切面
@Component // 将切面类注册为Spring Bean
public class AnnotatedOperateLogAspect {

    @Autowired //优先按类型注入
    //@Resource //按名称注入
    private OperatorLogService operatorLogService;

    /**
     * 拦截带有@Log注解的方法
     */
    //指定注解所在位置
    @Around("@annotation(org.fly.common.anno.Log)")
    public Object aroundAnnotatedAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();// 获取开始时间
        Object result = joinPoint.proceed();// 执行目标方法
        long endTime = System.currentTimeMillis();// 获取结束时间
        long costTime = endTime - startTime;// 计算耗时

        //构建操作日志对象
        OperateLog olog = new OperateLog();
        olog.setAdminId(getCurrentUserEmpId());// 当前用户ID
        olog.setOperationTime(LocalDateTime.now());// 操作时间
        olog.setOperationTarget(getOperationTarget(joinPoint));// 操作目标：0其它, 1管理员图书, 2读者图书, 3借阅记录, 4罚款记录, 5用户信息
        olog.setOperationType(getOperationType(joinPoint));// 1增2删3改
        olog.setMethodParams(Arrays.toString(joinPoint.getArgs()));// 参数任意类型任意大小
        olog.setIpAddress(getClientIpAddress());// IP地址
        olog.setReturnResult(result!=null?result.toString():null);// 返回结果
        olog.setCostTime(costTime);// 耗时

        log.info("操作日志：{}", olog);
        operatorLogService.add(olog);
        return result;
    }

    // 实现获取当前用户ID的逻辑CurrentHolder.getCurrentUserId()
    private Integer getCurrentUserEmpId() {
        return  CurrentHolder.getCurrentUserId();
    }
    // 获取当前请求对象
    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    // 在切面方法中添加获取IP的逻辑
    private String getClientIpAddress() {
        try {
            /**
             * X-Forwarded-For: 最常见的代理服务器转发头
             * X-Real-IP: Nginx等反向代理设置的真实IP
             * X-Original-Forwarded-For: 某些代理服务器使用的头
             * X-Originating-IP: 某些邮件网关使用的头
             * X-Cluster-Client-IP: 某些负载均衡器使用的头
             * CF-Connecting-IP: Cloudflare CDN使用的客户端IP
             * True-Client-IP: Akamai CDN使用的客户端IP
             */
            HttpServletRequest request = getCurrentRequest();//首先尝试从适用于代理服务器环境获取 IP
            String ip = request.getHeader("X-Forwarded-For");

            // 如果X-Forwarded-For有多个IP，取第一个
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("CF-Connecting-IP"); // Cloudflare
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("True-Client-IP"); // Akamai
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 处理 IPv6 本地回环地址
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }
            return ip;
        } catch (IllegalStateException e) {
            return "未知地址";
        }
    }

    //添加操作对象的判断逻辑 1图书, 2读者, 3借阅，4罚款，5还书
    //contains作用是判断字符串是否包含某个子串，返回true或false。
    private Integer getOperationTarget(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();

        // 基于类名判断模块
        if (className.contains("BookAdmin") ){
            return 1;// 实物图书
        } else if (className.contains("BookReader")) {
            return 2;// 元图书
        } else if (className.contains("BorrowRecord") ) {
            return 3;// 借阅
        }else if (className.contains("FineRecord")) {
            return 4;// 罚款
        } else if (className.contains("Reader")) {
            return 5;// 读者
        } else
            return 0;// 其它
    }
    //操作方法 1增2删3改4还5借
    private Integer getOperationType(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.contains("add")) {
            return 1;//添加
        } else if (methodName.contains("delete")){
            return 2;//删除
        } else if (methodName.contains("update")){
            return 3;//修改
        } else if (methodName.contains("return")) {
            return 4;//还书
        }else if (methodName.contains("borrow")) {
            return 5;//借书
        }
            return 0;// 其它
    }

}