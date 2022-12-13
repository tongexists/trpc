package tong.trpc.core.io.serialize.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.util.JacksonSerializerUtil;

import java.io.IOException;

/**
 * @Author tong-exists
 * @Create 2022/12/13 16:00
 * @Version 1.0
 */
public class TrpcResponseJacksonDeserializer extends StdDeserializer<TrpcResponse> {

    public TrpcResponseJacksonDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public TrpcResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        Integer code = null;
        String msg = null;
        String returnType = null;
        Object data = null;

        ObjectCodec codec = p.getCodec();
        TreeNode treeNode = codec.readTree(p);

        code = ((JsonNode) treeNode.get("code")).asInt();
        msg = ((JsonNode) treeNode.get("msg")).asText();
        returnType = ((JsonNode) treeNode.get("returnType")).asText();
        try {
            data = JacksonSerializerUtil.jsonToObject(((JsonNode)treeNode.get("data")).toString(), Class.forName(returnType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        TrpcResponse response = new TrpcResponse();
        response.setData(data);
        response.setMsg(msg);
        response.setCode(code);
        response.setReturnType(returnType);
        return response;
    }
}
