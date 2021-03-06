package project.diploma.mappings;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MappingsInitializer {
    private static final String ROOT_PACKAGE_NAME = "project.diploma";

    public static void initMappings(ModelMapper modelMapper) {
        String configureMappingsMethodName = ICustomMappings.class.getDeclaredMethods()[0]
                .getName();

        getClassesWithCustomMappings()
                .forEach(klass -> invokeMethodFromClass(klass, configureMappingsMethodName, modelMapper));
    }

    private static List<Class<?>> getClassesWithCustomMappings() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AssignableTypeFilter(ICustomMappings.class));

        var candidates = scanner.findCandidateComponents(MappingsInitializer.ROOT_PACKAGE_NAME);

        return candidates
                .stream()
                .map(BeanDefinition::getBeanClassName)
                .map(MappingsInitializer::getClassFromName)
                .filter(Objects::nonNull)
                .filter(ICustomMappings.class::isAssignableFrom)
                .collect(Collectors.toList());
    }

    private static Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void invokeMethodFromClass(Class<?> klass, String methodName, Object... params) {
        try {
            Method method = klass.getDeclaredMethod(methodName, ModelMapper.class);
            var obj = klass.newInstance();
            method.invoke(obj, params);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
