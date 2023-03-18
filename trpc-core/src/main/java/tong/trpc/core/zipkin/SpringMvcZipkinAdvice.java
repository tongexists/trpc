package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tong.trpc.core.util.FastjsonSerializerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @Author tong-exists
 * @Create 2023/3/16 17:40
 * @Version 1.0
 */
@Component
@Aspect
@Slf4j
public class SpringMvcZipkinAdvice {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(tong.trpc.core.annotation.TrpcTrace) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] argsRaw = joinPoint.getArgs();
        ArrayList<Object> argsList = new ArrayList<>(argsRaw.length);
        for (Object o : argsRaw) {
            if(o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof MultipartFile || o instanceof MultipartFile[]){
                continue;
            }
            argsList.add(o);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        Tracer tracer = ZipkinHolder.rpcTracing.tracing().tracer();
        Span span = tracer.newTrace().name(request.getRequestURI()).start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            ZipkinHolder.traceContextThreadLocal.set(span.context());
            span.tag("args", FastjsonSerializerUtil.objectToJson(argsList.toArray()));
            Object result = joinPoint.proceed();
            span.tag("result", FastjsonSerializerUtil.objectToJson(result));
            return result;
        } catch (RuntimeException | Error e) {
            span.error(e); // Unless you handle exceptions, you might not know the operation failed!
            throw e;
        } finally {
            span.finish(); // note the scope is independent of the span. Always finish a span.
            ZipkinHolder.traceContextThreadLocal.remove();
        }
    }
}
