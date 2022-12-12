package tong.trpc.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @Author tong-exists
 * @Create 2022/12/12 21:03
 * @Version 1.0
 */
public class JacksonXmlSerializerUtil {

    public static <T> String objToXml(T obj) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        return xmlMapper.writeValueAsString(obj);
    }

    public static <T> T xmlToObj(String xml, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        return xmlMapper.readValue(xml, clazz);
    }

}
