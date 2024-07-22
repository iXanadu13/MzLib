package mz.mzlib.util.delegator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DelegatorClassFinder(DefaultDelegatorClassFinder.class)
public @interface DelegatorClass
{
	Class<?> value();
}
