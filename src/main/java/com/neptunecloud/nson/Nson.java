package com.neptunecloud.nson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Created by Ryan Jairam on 10/05/2016.
 */
public class Nson {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Optional {

    }

    private static final Object mMutex = new Object();
    private static Nson mInstance = null;
    private final Gson mGson;

    public static Nson getInstance()
    {
        if (mInstance == null)
        {
            synchronized(mMutex)
            {
                if (mInstance == null)
                {
                    mInstance = new Nson();
                }
            }
        }
        return mInstance;
    }

    private Nson()
    {
        mGson = new GsonBuilder().create();
    }

    public static Object fromJson(String json, Class<?> c) throws NsonRequiredPropertyMissing, NsonException {
        Object object = getInstance().mGson.fromJson(json, c);


        Field[] fields = c.getDeclaredFields();
        for (Field field : fields)
        {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Optional.class))
            {
                try
                {
                    Field f = c.getDeclaredField(field.getName());
                    f.setAccessible(true);
                    Object obj = f.get(object);
                    if (obj == null)
                    {
                        throw new NsonRequiredPropertyMissing(field.getName());
                    }
                }
                catch (NoSuchFieldException e)
                {
                    throw new NsonException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new NsonException(e);
                }
            }
        }
        return object;
    }

    public static String toJson(Object o){
        return getInstance().mGson.toJson(o);
    }


    public static class NsonRequiredPropertyMissing extends Exception
    {
        private static final long serialVersionUID = 1L;

        public NsonRequiredPropertyMissing(String property)
        {
            super("The required property \"" + property + "\" is not present in the supplied JSON.");
        }
    }

    public static class NsonException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public NsonException(Exception e)
        {
            super(e);
        }
    }
}