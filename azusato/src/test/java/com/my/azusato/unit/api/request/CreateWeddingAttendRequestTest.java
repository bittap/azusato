package com.my.azusato.unit.api.request;

import javax.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.common.TestUtils;
import com.my.azusato.entity.WeddingAttender.Nationality;

public class CreateWeddingAttendRequestTest {

  Validator validator = TestUtils.getValidator();

  @BeforeEach
  void vertifygetValidIsValid() {
    var result = validator.validate(getValid());
    Assertions.assertTrue(result.isEmpty());
  }

  @Nested
  class attenderNumber {

    @Test
    void notNull() {
      var inValidParam = getValid();
      inValidParam.setAttenderNumber(null);

      var result = validator.validate(inValidParam);
      Assertions.assertFalse(result.isEmpty());
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals("{0}は必修項目です。", result.iterator().next().getMessage());
    }

    @Test
    void min() {
      var inValidParam = getValid();
      inValidParam.setAttenderNumber((byte) 0);

      var result = validator.validate(inValidParam);
      Assertions.assertFalse(result.isEmpty());
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals("最小1以上の{0}を入力してください。", result.iterator().next().getMessage());
    }
  }


  public static CreateWeddingAttendRequest getValid() {
    String name = "name";
    String nationality = Nationality.KOREA.toString();
    boolean attend = true;
    boolean eatting = true;
    String remark = "remark";
    byte attenderNumber = 10;

    return new CreateWeddingAttendRequest(name, nationality, attend, eatting, remark,
        attenderNumber);
  }
}
