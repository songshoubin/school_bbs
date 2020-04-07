package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController extends BaseController{

    @RequestMapping({"", "/", "index"})
    public String index(@RequestParam(name = "orderMode",defaultValue = "0") int orderMode) {
    	String order = "created";
    	//按热议
    	if(orderMode == 1)
    		order = "comment_count";
		// 1分页信息 2分类 3用户 4置顶  5精选 6排序
        IPage results = postService.paging(getPage(), null, null, null, null,order);
    	
        req.setAttribute("orderMode", orderMode);
        req.setAttribute("pageData", results);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }

    @RequestMapping("/search")
    public String search(String q) {

        IPage pageData = searchService.search(getPage(), q);

        req.setAttribute("q", q);
        req.setAttribute("pageData", pageData);
        return "search";
    }

}
