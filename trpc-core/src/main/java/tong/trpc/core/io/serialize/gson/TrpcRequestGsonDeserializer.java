package tong.trpc.core.io.serialize.gson;

import com.google.gson.*;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.util.GsonSerializerUtil;

import java.lang.reflect.Type;

/**
 * @Author tong-exists
 * @Create 2022/12/13 20:56
 * @Version 1.0
 */
public class TrpcRequestGsonDeserializer implements JsonDeserializer<TrpcRequest> {
    @Override
    public TrpcRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String className = null;
        String methodName = null;
        String[] paramsTypes = null;
        Object[] params = null;

        JsonObject jobj = json.getAsJsonObject();
        className = jobj.get("className").getAsString();
        methodName = jobj.get("methodName").getAsString();

        JsonArray paramTypeJsonArr = jobj.getAsJsonArray("paramsTypes");
        paramsTypes = new String[paramTypeJsonArr.size()];
        for (int i = 0; i < paramTypeJsonArr.size(); i++) {
            paramsTypes[i] = paramTypeJsonArr.get(i).getAsString();
        }

        JsonArray paramsArr = jobj.getAsJsonArray("params");
        params = new Object[paramsArr.size()];
        for (int i = 0; i < paramsArr.size(); i++) {
            try {
                params[i] = GsonSerializerUtil.fromJsonToObject(paramsArr.get(i).toString(), Class.forName(paramsTypes[i]));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        TrpcRequest request = new TrpcRequest(className, methodName, params, paramsTypes);
        return request;
    }
}
