package com.yxl.smmall.search.controller;

import com.yxl.smmall.search.service.MallSearchService;
import com.yxl.smmall.search.vo.SearchParam;
import com.yxl.smmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListPageController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * 自动将页面传递过来的所有查询请求参数成一个类
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model){
        try{
            SearchResult listParam  = mallSearchService.getList(searchParam);
            model.addAttribute("result",listParam);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "list";
    }



}
