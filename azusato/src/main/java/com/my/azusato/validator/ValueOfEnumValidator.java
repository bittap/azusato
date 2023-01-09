package com.my.azusato.validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.my.azusato.validator.annotation.ValueOfEnum;

/**
 * {@link ValueOfEnum}に対して検証を行う。
 * <p>
 * {@code null}の場合 || Enumにあるnameの場合 検証成功
 */
public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {

  /**
   * Enumにあるnameリスト
   */
  private List<String> acceptedValues;

  /**
   * Enumにあるnameリストを取得して{@link #acceptedValues}にセットする。
   */
  @Override
  public void initialize(ValueOfEnum constraintAnnotation) {
    this.acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
        .map(Enum::name).collect(Collectors.toList());

  }

  @Override
  public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
    if (Objects.isNull(value))
      return true;

    return acceptedValues.contains(value);
  }


}
