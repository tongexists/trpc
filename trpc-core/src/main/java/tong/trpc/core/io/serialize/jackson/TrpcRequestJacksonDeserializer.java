package tong.trpc.core.io.serialize.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.util.JacksonSerializerUtil;

import java.io.IOException;

/**
 * @Author tong-exists
 * @Create 2022/12/13 16:00
 * @Version 1.0
 */
public class TrpcRequestJacksonDeserializer extends StdDeserializer<TrpcRequest> {

    public TrpcRequestJacksonDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public TrpcRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String className = null;
        String methodName = null;
        String[] paramsTypes = null;
        Object[] params = null;

        ObjectCodec codec = p.getCodec();
        TreeNode treeNode = codec.readTree(p);

        className = ((JsonNode) treeNode.get("className")).asText();
        methodName = ((JsonNode) treeNode.get("methodName")).asText();

        ArrayNode paramsTypesNode = (ArrayNode) treeNode.get("paramsTypes");
        paramsTypes = new String[paramsTypesNode.size()];
        for (int i = 0; i < paramsTypesNode.size(); i++) {
            paramsTypes[i] = ((JsonNode) paramsTypesNode.get(i)).asText();
        }

        ArrayNode paramsNode = (ArrayNode) treeNode.get("params");
        params = new Object[paramsNode.size()];
        for (int i = 0; i < paramsNode.size(); i++) {
            JsonNode objNode = (JsonNode)paramsNode.get(i);
            Object obj = null;
            try {
                obj = JacksonSerializerUtil.jsonToObject(objNode.toString(), Class.forName(paramsTypes[i]));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            params[i] = obj;
        }

        TrpcRequest request = new TrpcRequest(className, methodName, params, paramsTypes);
        return request;
    }
}
