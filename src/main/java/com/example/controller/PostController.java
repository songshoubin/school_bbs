package com.example.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.lang.Result;
import com.example.config.RabbitConfig;
import com.example.entity.*;
import com.example.search.mq.PostMqIndexMessage;
import com.example.util.ValidationUtil;
import com.example.vo.CommentVo;
import com.example.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class PostController extends BaseController{

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id,@RequestParam(name = "orderMode",defaultValue = "0") int orderMode) {

        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        
        req.setAttribute("orderMode", orderMode);
        req.setAttribute("currentCategoryId", id);
        req.setAttribute("pn", pn);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {

        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", id));
        Assert.notNull(vo, "文章已被删除");

        postService.putViewCount(vo);

        // 1分页，2文章id，3用户id，排序
        IPage<CommentVo> results = commentService.paing(getPage(), vo.getId(), null, "created");

        req.setAttribute("currentCategoryId", vo.getCategoryId());
        req.setAttribute("post", vo);
        req.setAttribute("pageData", results);

        return "post/detail";
    }

    /**
     * 判断用户是否收藏了文章
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid) {
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        return Result.success(MapUtil.of("collection", count > 0 ));
    }

    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid) {
        Post post = postService.getById(pid);

        Assert.isTrue(post != null, "改帖子已被删除");
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        if(count > 0) {
            return Result.fail("你已经收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());

        collection.setPostUserId(post.getUserId());

        collectionService.save(collection);
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid) {
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "改帖子已被删除");

        collectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success();
    }

    @GetMapping("/post/edit")
    public String edit(){
        String id = req.getParameter("id");
        if(!StringUtils.isEmpty(id)) {
            Post post = postService.getById(id);
            Assert.isTrue(post != null, "改帖子已被删除");
            Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "没权限操作此文章");
            req.setAttribute("post", post);
        }
        req.setAttribute("categories", categoryService.list());
        return "/post/edit";
    }

    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post) {
    	
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if(validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        if(post.getId() == null) {//发帖
            post.setUserId(getProfileId());

            
            post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));// 转义HTML标记
            post.setContent(HtmlUtils.htmlEscape(post.getContent()));
            
            post.setTitle(sensitiveFilter.filter(post.getTitle()));//敏感词过滤
            post.setContent(sensitiveFilter.filter(post.getContent()));
            postService.save(post);

        } else {//编辑
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(), "无权限编辑此文章！");

            
            post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));// 转义HTML标记
            post.setContent(HtmlUtils.htmlEscape(post.getContent()));
            
            tempPost.setTitle(sensitiveFilter.filter(post.getTitle()));//敏感词过滤
            tempPost.setContent(sensitiveFilter.filter(post.getContent()));
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(tempPost);
        }

        // 通知消息给mq，告知更新或添加
        amqpTemplate.convertAndSend(RabbitConfig.es_exchage, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE));

        return Result.success().action("/post/" + post.getId());
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id) {
        Post post = postService.getById(id);

        Assert.notNull(post, "该帖子已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "无权限删除此文章！");

        postService.removeById(id);

        // 删除相关消息、收藏等
        messageService.removeByMap(MapUtil.of("post_id", id));
        collectionService.removeByMap(MapUtil.of("post_id", id));

        amqpTemplate.convertAndSend(RabbitConfig.es_exchage, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success().action("/user/index");
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid, String content) {
        Assert.notNull(jid, "找不到对应的文章");
        Assert.hasLength(content, "评论内容不能为空");

        Post post = postService.getById(jid);
        Assert.isTrue(post != null, "该文章已被删除");

        Comment comment = new Comment();
        comment.setPostId(jid);
        
        comment.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(content)));//敏感词过滤转义HTML标记
        
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        commentService.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);// 评论数量加一
        postService.updateById(post);

        postService.incrCommentCountAndUnionForWeekRank(post.getId(), true);// 本周热议数量加一

        if(comment.getUserId() != post.getUserId()) {// 通知作者，有人评论了你的文章,作者自己评论自己文章，不需要通知
            UserMessage message = new UserMessage();
            message.setPostId(jid);
            message.setCommentId(comment.getId());
            message.setFromUserId(getProfileId());
            message.setToUserId(post.getUserId());
            message.setType(1);
            post.setContent(HtmlUtils.htmlEscape(content));// 转义HTML标记
            message.setContent(sensitiveFilter.filter(post.getContent()));//敏感词过滤
            message.setCreated(new Date());
            message.setStatus(0);
            messageService.save(message);
            wsService.sendMessCountToUser(message.getToUserId()); // 即时通知作者（websocket）
        }

        if(content.startsWith("@")) {// 通知被@的人，有人回复了你的评论
            String username = content.substring(1, content.indexOf(" "));
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if(user != null) {
                UserMessage message = new UserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0);
                messageService.save(message);
            }
        }
        return Result.success().action("/post/" + post.getId());
    }
    
    //评论点赞
    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-zan/")
    public Result zan(Long id,boolean ok) {
    	Assert.notNull(id, "评论id不能为空！");
    	Comment comment = commentService.getById(id);
    	if(ok == false) {
    		if(comment.getVoteUp() == 0)
    			comment.setVoteUp(0);
    		else
    			comment.setVoteUp(comment.getVoteUp()-1);
    	}
    	else 
    		comment.setVoteUp(comment.getVoteUp()+1);
        Assert.notNull(comment, "找不到对应评论！");
        commentService.updateById(comment);
        return Result.success(null);
    }
    
    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result reply(Long id) {

        Assert.notNull(id, "评论id不能为空！");

        Comment comment = commentService.getById(id);

        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.saveOrUpdate(post);

        //评论数量减一
        postService.incrCommentCountAndUnionForWeekRank(comment.getPostId(), false);

        return Result.success(null);
    }

}
