package org.someth2say.storm.index;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

public class IndexEntryBuilder {

    public static <T> T build(Class<T> targetClass, final String params) {
        return buildWithSingleParamFactoryMethod(targetClass, params);
    }

    public static <T> T build(Class<T> targetClass) {
        return buildWithNoParamFactoryMethod(targetClass);
    }

    private static  <T> T buildWithNoParamFactoryMethod(Class<T> targetClass) {
        try {
            final Constructor<T> declaredConstructor = targetClass.getConstructor();
            return declaredConstructor.newInstance();
        } catch (InvocationTargetException e) {
            throw new NoSuchMethodError("No-param constructor exception for class "+targetClass.getName()+"\n"+e.getMessage());
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            List<Method> factoryMethods = Arrays.asList(targetClass.getMethods()).stream()
            .filter(m->Modifier.isStatic(m.getModifiers()))
            .filter(m->targetClass.isAssignableFrom(m.getReturnType()))
            .filter(m->(m.getParameterCount()==0))
            .collect(Collectors.toList());

            if (factoryMethods.size()==0){
                throw new NoSuchMethodError("No no-params factory methods found for "+targetClass.getName());
            }

            if (factoryMethods.size()>1){
                throw new NoSuchMethodError("Multiple potential no-params factory methods for "+targetClass.getName());
            }
            
            Method method = factoryMethods.get(0);
            try {
                return (T) method.invoke(null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                throw new NoSuchMethodError("Can not execute no-params factory method "+method.getName()+".\n"+e1.getMessage());

            }
        }
    }

    private static <T> T buildWithSingleParamFactoryMethod(Class<T> targetClass, String params) {
            try {
                final Constructor<T> declaredConstructor = targetClass.getConstructor(String.class);
                return declaredConstructor.newInstance(params);
            } catch (InvocationTargetException e) {
                throw new NoSuchMethodError("Single-param constructor exception for class "+targetClass.getName()+"\n"+e.getMessage());
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                List<Method> factoryMethods = Arrays.asList(targetClass.getMethods()).stream()
                    .filter(m->Modifier.isStatic(m.getModifiers()))
                    .filter(m->targetClass.isAssignableFrom(m.getReturnType()))
                    .filter(m->(m.getParameterCount()==1))
                    .filter(m->(m.getParameterTypes()[0].isAssignableFrom(String.class)))
                    .collect(Collectors.toList());
                if (factoryMethods.size()==0){
                    throw new NoSuchMethodError("No single-param factory methods found for "+targetClass.getName());
                }

                if (factoryMethods.size()>1){
                    throw new NoSuchMethodError("Multiple potential single-param factory methods for "+targetClass.getName());
                }
                Method method = factoryMethods.get(0);
                try {
                    return (T) method.invoke(null, params);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    throw new NoSuchMethodError("Can not execute factory method "+method.getName()+".\n"+e1.getMessage());
                }
            }

    }
}
