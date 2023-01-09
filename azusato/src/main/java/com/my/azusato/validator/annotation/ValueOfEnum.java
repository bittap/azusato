package com.my.azusato.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import com.my.azusato.validator.ValueOfEnumValidator;

/**
 * Enumタイプのvalueが含まれているか検証する。
 * <p>
 * 検証は{@link ValueOfEnumValidator}にて行われる。
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface ValueOfEnum {

  Class<? extends Enum<?>> enumClass();

  String message() default "{inValidation}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
