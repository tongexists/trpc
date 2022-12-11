package tong.trpc.core.spring;

import org.springframework.beans.factory.FactoryBean;

public class TrpcSpringFactoryBean implements FactoryBean<Object> {

    //返回的对象实例
    private Object object;

    //Bean的类型
    private Class<?> type;

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

}
