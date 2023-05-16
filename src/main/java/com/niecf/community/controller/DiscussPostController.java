package com.niecf.community.controller;

import com.niecf.community.entity.Comment;
import com.niecf.community.entity.DiscussPost;
import com.niecf.community.entity.Page;
import com.niecf.community.entity.User;
import com.niecf.community.service.CommentService;
import com.niecf.community.service.DiscussPostService;
import com.niecf.community.service.LikeService;
import com.niecf.community.service.UserService;
import com.niecf.community.util.CommunityConstant;
import com.niecf.community.util.CommunityUtil;
import com.niecf.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user=hostHolder.getUsers();
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没有登陆！");
        }

        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJSONString(0,"发布成功！");
    }
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post=discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        User user= userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);

        int likeStatus=hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        List<Comment> commentList=commentService.findCommentsByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(), page.getLimit());

        List<Map<String,Object>> commentVoList=new ArrayList<>();
        if(commentList!=null){
            for(Comment comment:commentList){
                Map<String,Object> commentVo=new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);

                likeStatus=hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                List<Comment> replyList=commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList=new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo=new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        User target=reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);

                        likeStatus=hostHolder.getUsers()==null?0:likeService.findEntityLikeStatus(hostHolder.getUsers().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                int replyCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
