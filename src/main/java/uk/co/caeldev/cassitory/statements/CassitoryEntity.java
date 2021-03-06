package uk.co.caeldev.cassitory.statements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CassitoryEntity {

    String[] tables();

    String destinationPackage() default "";

}
