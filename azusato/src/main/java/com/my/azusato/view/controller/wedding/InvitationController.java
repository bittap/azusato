package com.my.azusato.view.controller.wedding;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.my.azusato.view.controller.common.UrlConstant;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = {UrlConstant.WEDDING_CONTROLLER_REQUSET,
    UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.WEDDING_CONTROLLER_REQUSET,
    UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.WEDDING_CONTROLLER_REQUSET,})
@Controller
@RequiredArgsConstructor
public class InvitationController {

  private final static String VIEW_FOLDER_NAME = "wedding/";

  @GetMapping("invitation")
  public ModelAndView write() {
    ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "invitation");
    return mav;
  }
}
