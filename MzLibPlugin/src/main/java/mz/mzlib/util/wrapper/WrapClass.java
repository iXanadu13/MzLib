package mz.mzlib.util.wrapper;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@WrappedClassFinderClass(WrapClass.Finder.class)
public @interface WrapClass
{
    Class<?> value();

    class Finder extends WrappedClassFinder
    {
        public Class<?> find(Class<? extends WrapperObject> wrapperClass, Annotation annotation)
        {
            return ((WrapClass) annotation).value();
        }
    }
}
