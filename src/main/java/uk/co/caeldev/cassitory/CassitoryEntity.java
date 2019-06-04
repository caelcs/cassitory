package uk.co.caeldev.cassitory;

import com.datastax.driver.core.ConsistencyLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CassitoryEntity {

    Class<?>[] target();

    String destinationPackage() default "";

    ConsistencyLevel consistencyLevel() default ConsistencyLevel.QUORUM;

}
