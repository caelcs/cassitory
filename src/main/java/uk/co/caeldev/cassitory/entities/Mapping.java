package uk.co.caeldev.cassitory.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(Mappings.class)
@Target(ElementType.FIELD)
public @interface Mapping {
    Class<?>[] target();
    String field() default "";
}