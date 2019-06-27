package uk.co.caeldev.cassitory.statements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(Mappings.class)
@Target(ElementType.FIELD)
public @interface Mapping {
    String table();
    String field() default "";
    boolean pk() default false;
}