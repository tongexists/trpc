package tong.trpc.core.domain;

/**
 * 常量
 */
public class TrpcConstant {
    public final static short MAGIC = 0xca; // 魔树

    public final static int HEAD_TOTAL_LEN = 16; //header总的字节数量
    public final static int HEART_BEAT_INTERNAL = 4; //心跳间隔
    public final static int IDLE_THRESHOLD = 60; // 空闲阈值，若客户端超过IDLE_THRESHOLD秒未与服务端通信，服务端会断开连接
    /**
     * Trpc服务接口类型的前缀
     */
    public final static String TRPC_SERVICE_PRE_CLASS_NAME = "Trpc";
}
