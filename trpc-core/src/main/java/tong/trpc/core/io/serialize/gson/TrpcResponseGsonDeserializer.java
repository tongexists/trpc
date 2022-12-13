package tong.trpc.core.io.serialize.gson;

import com.google.gson.*;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.util.GsonSerializerUtil;

import java.lang.reflect.Type;

/**
 * @Author tong-exists
 * @Create 2022/12/13 20:56
 * @Version 1.0
 */
public class TrpcResponseGsonDeserializer implements JsonDeserializer<TrpcResponse> {
    @Override
    public TrpcResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Integer code = null;
        String msg = null;
        String returnType = null;
        Object data = null;

        JsonObject jobj = json.getAsJsonObject();
        code = jobj.get("code").getAsInt();
        msg = jobj.get("msg").getAsString();
        returnType = jobj.get("returnType").getAsString();
        try {
            data = GsonSerializerUtil.fromJsonToObject(jobj.get("data").toString(), Class.forName(returnType));
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
