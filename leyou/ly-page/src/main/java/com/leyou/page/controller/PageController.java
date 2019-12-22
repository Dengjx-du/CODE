package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;
    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("msg","这是helloworld！！");
        return "hello";
    }

    /**
     * 显示商品详情页
     * @param model
     * @return
     */
    @GetMapping("/item/{id}.html")
    public String itemPage(Model model, @PathVariable(name = "id")Long spuId){
        model.addAllAttributes(pageService.loadData(spuId));
        return "item";
    }

}
