package com.example.template;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.templates.DirectiveHandler;
import com.example.common.templates.TemplateDirective;
import com.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    PostService postService;

    @Override
    public String getName() {
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {

    	Integer orderMode = handler.getInteger("orderMode");
        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn", 1);
        Integer size = handler.getInteger("size", 2);
        Long categoryId = handler.getLong("categoryId");

        String order = "created";
        //默认按最新，否则按热议
        if(orderMode != null&&orderMode == 1)
    		order = "comment_count";
        IPage page = postService.paging(new Page(pn, size), categoryId, null, level, null, order);

        handler.put(RESULTS, page).render();
    }
}
