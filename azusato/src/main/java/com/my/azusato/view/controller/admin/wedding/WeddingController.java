package com.my.azusato.view.controller.admin.wedding;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

@RequestMapping(value = {UrlConstant.WEDDING_ADMIN_CONTROLLER_REQUSET,
    UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.WEDDING_ADMIN_CONTROLLER_REQUSET,
    UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.WEDDING_ADMIN_CONTROLLER_REQUSET,})
@Controller
public class WeddingController {

  public static final String FOLDER_NAME = "admin/wedding";

  @GetMapping
  public ModelAndView index() {
    ModelAndView mav = new ModelAndView();

    mav.setViewName(FOLDER_NAME + "/index");
    // make home icon active
    HeaderReponse hr = new HeaderReponse();
    hr.setWedding(true);
    mav.addObject(ModelConstant.HEADER_KEY, hr);

    return mav;
  }
}
