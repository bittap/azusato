package com.my.azusato.view.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.my.azusato.view.controller.common.UrlConstant;

@Controller
@RequestMapping(value = {UrlConstant.SCRIPT_TEST_CONTROLLER_REQUSET,})
public class TestScriptController {

  public static final String FOLDER = "/test";

  @GetMapping("/paging")
  public String paging() {
    return FOLDER + "/paging";
  }
}
