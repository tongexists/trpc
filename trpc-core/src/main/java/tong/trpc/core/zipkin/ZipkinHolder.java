package tong.trpc.core.zipkin;

import brave.Tracing;
import brave.propagation.TraceContext;
import brave.rpc.RpcTracing;
import tong.trpc.core.io.handler.TrpcServerHandler;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author tong-exists
 * @Create 2023/3/16 16:31
 * @Version 1.0
 */
public class ZipkinHolder {

    public static ThreadLocal<TraceContext> traceContextThreadLocal = new ThreadLocal<>();

    public static RpcTracing rpcTracing;

    static {
        Properties properties = new Properties();
        try {
            properties.load(TrpcServerHandler.class.getClassLoader().getResourceAsStream("trpc.properties"));
            String serviceName = (String) properties.get("serviceName");
            if (serviceName == null) {
                throw new RuntimeException("未在trpc.properties配置serviceName");
            }
            String serverAddress = (String) properties.getOrDefault("serverAddress", "127.0.0.1");
            String serverPortStr = (String) properties.get("serverPort");
            if (serverPortStr == null) {
                throw new RuntimeException("未在trpc.properties配置serverPort");
            }
            int serverPort = Integer.parseInt(serverPortStr);

            rpcTracing =  RpcTracing.create(
                    Tracing.newBuilder()
                            .localServiceName(serviceName)
                            .localPort(serverPort)
                            .localIp(serverAddress)
                            .addSpanHandler(AsyncZipkinSpanHandler.create(URLConnectionSender.create("http://localhost:9411/api/v2/spans")))
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}