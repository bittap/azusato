package com.my.azusato.common;

import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * for testing common methods.
 * 
 * @author Carmel
 *
 */
public class TestUtils {


  public static Validator getValidator() {
    var facoty = Validation.buildDefaultValidatorFactory();
    return facoty.getValidator();
  }


  /**
   * delegate {@link TestUtils#getLastElement(List, String)}, supplying no for the fieldName
   * argument.
   * 
   * @param <T> class
   * @param lists target list
   * @return last element of list
   */
  public static <T> T getLastElement(List<T> lists) {
    return TestUtils.getLastElement(lists, "no");
  }

  /**
   * this use stream and sorts for fieldName
   * 
   * @param <T> class
   * @param lists target list
   * @param fieldName get method's fieldName
   * @return last element of list
   */
  public static <T> T getLastElement(List<T> lists, String fieldName) {
    return lists.stream().sorted((e1, e2) -> {
      long e1No = (long) ReflectionTestUtils.getField(e1, fieldName);
      long e2No = (long) ReflectionTestUtils.getField(e2, fieldName);
      return (int) (e2No - e1No);
    }).findFirst().get();
  }

  /**
   * delegate {@link TestUtils#excludeColumn(Object, List)}, supplying no for fieldNames argument.
   * 
   * @param <T> class
   * @param target target class
   * @return a class excluded for no field
   */
  public static <T> T excludeColumn(T target) {
    List<String> fieldNames = List.of("no");
    return excludeColumn(target, fieldNames);
  }

  /**
   * exclude fieldNames. this use iterator and {@link ReflectionTestUtils} for accessing field
   * 
   * @param <T> class
   * @param target target class
   * @param fieldNames get method's fieldName
   * @return a class excluded for fieldNames field
   */
  public static <T> T excludeColumn(T target, List<String> fieldNames) {

    for (String fieldName : fieldNames) {
      ReflectionTestUtils.setField(target, fieldName, null);
    }

    return target;
  }
}
