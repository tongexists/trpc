package tong.trpc.core.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring工具类
 * @Author tong-exists
 * @Create 2023/3/5 10:48
 * @Version 1.0
 */
public class SpringUtil {
    /**
     * 获取指定basePackage下所有在类上标记的某种注解
     * @param basePackage 基包
     * @param resourcePattern
     * @param annotationClass 要找的注解
     * @return 获取指定basePackage下所有在类上标记的某种注解
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public static List<Annotation> findAnnotations(String basePackage, String resourcePattern, Class<? extends Annotation> annotationClass) throws IOException, ClassNotFoundException {
        //spring工具类，可以获取指定路径下的全部类
        List<Annotation> list = new ArrayList<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(basePackage) + resourcePattern;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerfactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                //判断是否有指定主解
                Annotation anno = clazz.getAnnotation(annotationClass);
                if (anno != null) {
                    list.add(anno);
                }
            }
            return list;
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }

    }
}
