package com.raju.blogapp.controller;

import com.raju.blogapp.entity.Comment;
import com.raju.blogapp.entity.Post;
import com.raju.blogapp.entity.Tag;
import com.raju.blogapp.entity.User;
import com.raju.blogapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class PostController {
    private Date createdAt;
    @Autowired
    private Post post;
    @Autowired
    private Comment comment;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;

    private String searchString;

    @Autowired
    private UserService userService;

    private List<Post> tagPosts = new ArrayList<>();

    private Set<Post> authorPosts = new HashSet<>();

    private List<Integer> activeTags = new ArrayList<>();

    private Set<Integer> activeAuthorIds = new HashSet<>();

    @GetMapping("/newpost")
    public String newPost(Model model){
        createdAt = postService.formatDate(new Date());
        model.addAttribute("post",post);
        return "post-form";
    }

    public User getLoggedUserDetails(String username){
        Optional<User> userDetails = userService.findUser(username);
        if(userDetails.isPresent()){
           User user = userDetails.get();
            return user;
        }
        return null;
    }

    @GetMapping("/")
    public String showPosts(Model model, Principal principal){
        Boolean loggedInUser;
        tagPosts.clear();
        activeAuthorIds.clear();
        activeTags.clear();
        authorPosts.clear();

        if(principal==null){
            loggedInUser = Boolean.FALSE;
        }
        else{
            loggedInUser = Boolean.TRUE;
        }

        List <Tag> savedTags = tagService.findAllTags();
        List <User> authors = userService.findAllUsers();
        model.addAttribute("savedTags",savedTags);
        model.addAttribute("loggedInUser",loggedInUser);
        model.addAttribute("authors",authors);
        return findPaginated(1,model,"asc","",principal);
    }

    @PostMapping("/")
    public String submitPost(HttpServletRequest request, @ModelAttribute("post") Post post, Model model,Principal principal){
        Date updatedAt = postService.formatDate(new Date());
        String[] tagsList = request.getParameter("allTags").split(",");
        for(String tagName : tagsList){
            if(tagName!=""){
                Tag tag = tagService.getTag(tagName.toLowerCase());
                if(tag == null){
                    Tag newTag = new Tag();
                    newTag.setName(tagName.toLowerCase());
                    Date TagCreatedAt = postService.formatDate(new Date());
                    Date TagUpdatedAt = postService.formatDate(new Date());
                    newTag.setCreatedAt(TagCreatedAt);
                    newTag.setUpdatedAt(TagUpdatedAt);
                    tagService.saveTag(newTag);
                    post.getTags().add(newTag);
                }
                else {
                    post.getTags().add(tag);
                }
            }
        }
        post.setAuthor(principal.getName());
        post.setUpdatedAt(updatedAt);
        String content = post.getContent();
        String excerpt = content.substring(0,240);
        post.setExcerpt(excerpt);
        post.setCreatedAt(createdAt);
        String isPublished = request.getParameter("isPublished");
        if(isPublished.equals("0")){
            post.setIsPublished(Boolean.FALSE);
        }else{
            post.setIsPublished(Boolean.TRUE);
            Date publishedAt = postService.formatDate(new Date());
            post.setPublishedAt(publishedAt);
        }
        postService.savePost(post);
          return "redirect:/";
    }
    @PostMapping("/publishPost{Id}")
    public String publishPost(@PathVariable(value = "Id") int id){
        Post post = postService.getPost(id);
        post.setIsPublished(Boolean.TRUE);
        Date updatedAt = postService.formatDate(new Date());
        post.setUpdatedAt(updatedAt);
        post.setPublishedAt(updatedAt);
        postService.savePost(post);
        return "redirect:/";
    }

    @GetMapping("/post{Id}")
    public String showPost(@PathVariable(value="Id") int id,Model model,Principal principal){
        Boolean loggedInUser;
        String loggedInUsername=null;
        String loggedInUserRole = null;
        if(principal==null){
            loggedInUser = Boolean.FALSE;
        }
        else{
            loggedInUser = Boolean.TRUE;
            loggedInUsername = principal.getName();
            loggedInUserRole = getLoggedUserDetails(loggedInUsername).getRole();
        }
        Post post = postService.getPost(id);
        List<Tag> postTags = post.getTags();
        List<Comment> postComments = post.getComments();
        model.addAttribute("post",post);
        model.addAttribute("comments",postComments);
        model.addAttribute("postTags",postTags);
        model.addAttribute("loggedInUser",loggedInUser);
        model.addAttribute("loggedInUsername",loggedInUsername);
        model.addAttribute("loggedInUserRole",loggedInUserRole);
        return "show-post";
    }

    @PostMapping("/post{Id}")
    public String editPost(@PathVariable(value="Id") int id,Model model){
        Post post = postService.getPost(id);
        List<String> tags = new ArrayList<>();
        for(Tag tag : post.getTags()){
            tags.add(tag.getName());
        }
        String allTags = String.join(",",tags);
        model.addAttribute("post",post);
        model.addAttribute("allTags",allTags);
        return "edit-post";
    }

    @PostMapping("/updatePost{Id}")
    public String updatePost(@ModelAttribute("post") Post post,HttpServletRequest request,@PathVariable(value="Id") int id){//        String[] tagsList = request.getParameter("allTags").split(",");
        String [] tagsList = request.getParameter("allTags").split(",");
        for(String tagName : tagsList){
            if(tagName!=""){
                Tag tag = tagService.getTag(tagName.toLowerCase());
                if(tag == null){
                    Tag newTag = new Tag();
                    newTag.setName(tagName.toLowerCase());
                    Date tagUpdatedAt = postService.formatDate(new Date());
                    Date tagCreatedAt = postService.formatDate(new Date());
                    newTag.setUpdatedAt(tagUpdatedAt);
                    newTag.setCreatedAt(tagCreatedAt);
                    tagService.saveTag(newTag);
                    post.getTags().add(newTag);
                }
                else {
                    post.getTags().add(tag);
                    }
                }
            }
        Post oldPost = postService.getPost(id);
        Date updatedAt = postService.formatDate(new Date());
        post.setCreatedAt(oldPost.getCreatedAt());
        post.setPublishedAt(oldPost.getPublishedAt());
        post.setIsPublished(oldPost.getIsPublished());
        post.setExcerpt(post.getContent().substring(0,240));
        post.setUpdatedAt(updatedAt);
        post.setAuthor(oldPost.getAuthor());
        post.setComments(oldPost.getComments());
        postService.savePost(post);
        return "redirect:/post"+id;
    }
    @PostMapping("/post{Id}/addComment")
    public String addComment(HttpServletRequest request,@PathVariable(value="Id") int id,Model model){
        Comment comment = new Comment();
        comment.setName(request.getParameter("name"));
        comment.setEmail(request.getParameter("email"));
        comment.setComment(request.getParameter("comment"));
        Post post = postService.getPost(id);
        Date createdAt = postService.formatDate(new Date());
        comment.setCreatedAt(createdAt);
        comment.setUpdatedAt(createdAt);
        post.addComment(comment);
        model.addAttribute("comments",post.getComments());
        postService.savePost(post);
        return "redirect:/post"+id;
    }

    @GetMapping("/post{postId}/editComment{commentId}")
    public String editComment(Principal principal,@PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId,Model model){
        Boolean loggedInUser;
        String loggedInUsername=null;
        String loggedInUserRole = null;
        if(principal==null){
            loggedInUser = Boolean.FALSE;
        }
        else{
            loggedInUser = Boolean.TRUE;
            loggedInUsername = principal.getName();
            loggedInUserRole = getLoggedUserDetails(loggedInUsername).getRole();
        }
        Post post = postService.getPost(postId);
        Comment oldComment = commentService.getComment(commentId);
        String oldCommentMessage = oldComment.getComment();
        List <Comment> comments = post.getComments();
        model.addAttribute("post",post);
        model.addAttribute("comments",comments);
        model.addAttribute("oldCommentId",commentId);
        model.addAttribute("oldCommentMessage",oldCommentMessage);
        model.addAttribute("loggedInUsername",loggedInUsername);
        model.addAttribute("loggedInUserRole",loggedInUserRole);
        return "show-post";
    }

    @RequestMapping("/post{postId}/updateComment{commentId}")
    public String updateComment(HttpServletRequest request,@PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId){
        String newComment= request.getParameter("newComment");
        Date updatedAt = postService.formatDate(new Date());
        Comment comment = commentService.getComment(commentId);
        comment.setComment(newComment);
        comment.setUpdatedAt(updatedAt);
        commentService.saveComment(comment);
        return "redirect:/post"+postId;
    }

    @RequestMapping("/post{postId}/deleteComment{commentId}")
    public String deleteComment(HttpServletRequest request,@PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId){
        commentService.deleteComment(commentId);
        return "redirect:/post"+postId;
    }

    @GetMapping("/page/{pageNumber}/filters")
    public String findFilters(@PathVariable(value="pageNumber")int pageNo, Model model,
                                  @RequestParam(value = "tagSortOrder",defaultValue = "asc") String sortOrder,
                                  @RequestParam(value = "tagId",defaultValue="") List<String> tagId,
                                  @RequestParam(value = "tagSearch",defaultValue = "") String searchField,
                              @RequestParam(value = "authorId",defaultValue = "") List<String> authorId,
                              Principal principal
                              ){
        Boolean loggedInUser;
        tagPosts.clear();
        authorPosts.clear();
        String loggedInUsername=null;
        String loggedInUserRole =null;

        if(principal==null){
            loggedInUser = Boolean.FALSE;
        }
        else{
            loggedInUser = Boolean.TRUE;
            loggedInUsername = principal.getName();
            loggedInUserRole = getLoggedUserDetails(loggedInUsername).getRole();
        }

        String sortField = "publishedAt";
        if(!Objects.equals(searchString, "")){
            searchField = searchString;
            searchString="";
        }

        if(!authorId.isEmpty()){
            activeAuthorIds.clear();
            for(String activeAuthorId : authorId) {
                activeAuthorIds.add(Integer.parseInt(activeAuthorId));
            }
        }

        int pageSize=10;
        List <Tag> savedTags = tagService.findAllTags();
        if(!tagId.isEmpty()){
            for(String activeTagId : tagId) {
                activeTags.add(Integer.parseInt(activeTagId));
            }
        }

        if(!activeTags.isEmpty()){
            tagPosts.clear();
            for(Integer activeTagId : activeTags){
                Tag tag = tagService.getTag(activeTagId);
                for(Post post : tag.getPosts()){
                    if(post.getTitle().toLowerCase().contains(searchField.toLowerCase())){
                        if(!tagPosts.contains(post)){
                            tagPosts.add(post);
                        }
                    }
                    if(post.getContent().toLowerCase().contains(searchField.toLowerCase())){
                        if(!tagPosts.contains(post)){
                            tagPosts.add(post);
                        }
                    }
                }
            }
        }

        if(!authorId.isEmpty()){
            for(String activeAuthorId : authorId){
                activeAuthorIds.add(Integer.parseInt(activeAuthorId));
            }
        }

        if(!activeAuthorIds.isEmpty()){
            authorPosts.clear();
            for(Integer activeAuthorId : activeAuthorIds){
                User user=null;
                Optional <User> userDetails = userService.findUserById(activeAuthorId);
                if(userDetails.isPresent()){
                    user = userDetails.get();
                    authorPosts.addAll(postService.findAllAuthorPosts(user.getName(),searchField));
                }
            }
        }

        if(activeTags.isEmpty() && !activeAuthorIds.isEmpty()){
            tagPosts.addAll(authorPosts);
        } else if (!activeTags.isEmpty() && activeAuthorIds.isEmpty()) {
            tagPosts.addAll(authorPosts);
        } else{
            tagPosts.retainAll(authorPosts);
        }

        if(Objects.equals(sortOrder, "asc")){
            Collections.sort(tagPosts, new Comparator<Post>() {
                @Override
                public int compare(Post post1, Post post2) {
                    if(post1.getPublishedAt()!=null && post2.getPublishedAt()!=null){
                        return post1.getPublishedAt().compareTo(post2.getPublishedAt());
                    }
                    else{
                        if(post1.getPublishedAt()==null || post2.getPublishedAt()==null){
                            return 0;
                        }
                        else{
                            return post1.getPublishedAt().compareTo(post2.getPublishedAt());
                        }
                    }
                }
            });
        }

        if(Objects.equals(sortOrder, "desc")){
            Collections.sort(tagPosts, new Comparator<Post>() {
                @Override
                public int compare(Post post1, Post post2) {
                    if(post1.getPublishedAt()!=null && post2.getPublishedAt()!=null){
                        return post2.getPublishedAt().compareTo(post1.getPublishedAt());
                    }
                    else{
                        if(post1.getPublishedAt()==null || post2.getPublishedAt()==null){
                            return 0;
                        }
                        else{
                            return post2.getPublishedAt().compareTo(post1.getPublishedAt());
                        }
                    }
                }
            });
        }

        List <User> authors = userService.findAllUsers();

        if(tagId.isEmpty() && activeTags.isEmpty() && activeAuthorIds.isEmpty()){
            return findPaginated(1,model,"asc","",principal);
        }

        PagedListHolder page = new PagedListHolder(tagPosts);
        page.setPageSize(pageSize);
        page.setPage(pageNo-1);
        List <Post> posts = page.getPageList();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getPageCount());
        model.addAttribute("posts",posts);
        model.addAttribute("sortField",sortField);
        model.addAttribute("tagSortOrder",sortOrder);
        model.addAttribute("savedTags",savedTags);
        model.addAttribute("allPosts",tagPosts.size());
        model.addAttribute("activeTags",activeTags);
        model.addAttribute("tagSearch",searchField);
        model.addAttribute("tagId",tagId);
        model.addAttribute("authorId",authorId);
        model.addAttribute("loggedInUser",loggedInUser);
        model.addAttribute("loggedInUsername",loggedInUsername);
        model.addAttribute("loggedInUserRole",loggedInUserRole);
        model.addAttribute("activeAuthorIds",activeAuthorIds);
        model.addAttribute("authors",authors);
        return "show-posts-with-tags";
    }

    @GetMapping("/page/{pageNumber}")
    public String findPaginated(@PathVariable(value="pageNumber")int pageNo,Model model,
                                @RequestParam(value = "sortOrder",defaultValue = "asc") String sortOrder,
                                @RequestParam(value = "search",defaultValue = "") String searchField,
                                Principal principal
                                ){
        Boolean loggedInUser;
        String loggedInUsername = null;
        String loggedInUserRole = null;
        if(principal==null){
            loggedInUser = Boolean.FALSE;
        }
        else{
            loggedInUser = Boolean.TRUE;
            loggedInUsername = principal.getName();
            loggedInUserRole = getLoggedUserDetails(loggedInUsername).getRole();
        }
        String sortField = "publishedAt";
        searchString = searchField;
        activeTags.clear();
        int pageSize=10;
        List <Tag> savedTags = tagService.findAllTags();
        List <User> authors = userService.findAllUsers();
        Page<Post> page = postService.findPostsWithPagination(pageNo-1,pageSize,sortField,sortOrder,searchField.toLowerCase());
        List <Post> posts = page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("allPosts",page.getTotalElements());
        model.addAttribute("posts",posts);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortOrder",sortOrder);
        model.addAttribute("savedTags",savedTags);
        model.addAttribute("search",searchField);
        model.addAttribute("loggedInUser",loggedInUser);
        model.addAttribute("loggedInUsername",loggedInUsername);
        model.addAttribute("loggedInUserRole",loggedInUserRole);
        model.addAttribute("authors",authors);
        return "show-posts";
    }

    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable(value = "postId") Integer postId){
        postService.deleteThePost(postId);
        return "redirect:/";
    }
}
