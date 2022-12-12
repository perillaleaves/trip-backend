package mini.board.domain.post;

import mini.board.domain.user.UserDTO;
import mini.board.exception.APIError;
import mini.board.response.ApiResponse;
import mini.board.response.ErrorResponse;
import mini.board.response.Response;
import mini.board.response.ValidateResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 9. 게시글 작성
    @PostMapping("/post")
    public Response<Post> create(@RequestBody Post post, HttpServletRequest request) {

        try {
            Post findPost = postService.create(post, request);
            return new Response<>(findPost);
        } catch (APIError e) {
            return new Response<>(new ErrorResponse(e.getCode(), e.getMessage()));
        }

    }

    // 10. 게시글 리스트 조회
    @GetMapping("/posts")
    public Response<ApiResponse> posts(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        List<Post> posts = postService.getPosts();
        List<PostDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostDTO postDTO = PostDTO.boardList(post);
            postDTOs.add(postDTO);
        }
        return new Response<>(new ApiResponse(postDTOs));
    }

    // 11. 게시물 상세 조회
    @GetMapping("/post/{post_id}")
    public Response<ApiResponse> post(@PathVariable("post_id") Long postId) {

        Post post = postService.getPost(postId).get();
        PostDTO postDTO = PostDTO.board(post);

        return new Response<>(new ApiResponse(postDTO));
    }

    // 12. 게시글 수정
    @PutMapping("/post/{post_id}")
    public Response<ApiResponse> update(@PathVariable("post_id") Long postId, @RequestBody Post post, HttpServletRequest request) {

        try {
            Post updatedPost = postService.updatePost(postId, post, request);
            PostDTO postDTO = new PostDTO(updatedPost.getId(), updatedPost.getTitle(), updatedPost.getContent(), updatedPost.getCreatedAt(), updatedPost.getUpdatedAt(), updatedPost.getCommentSize(),
                    new UserDTO(updatedPost.getUser().getId(), updatedPost.getUser().getName(), updatedPost.getUser().getCreatedAt(), updatedPost.getUser().getUpdatedAt()));

            return new Response<>(new ApiResponse(postDTO));
        } catch (APIError e) {
            return new Response<>(new ErrorResponse(e.getCode(), e.getMessage()));
        }

    }

    // 16. 게시글 삭제
    @DeleteMapping("/post/{post_id}")
    public Response<ApiResponse> delete(@PathVariable("post_id") Long postId, HttpServletRequest request) {

        try {
            postService.delete(postId, request);
            return new Response<>(new ValidateResponse("delete", "게시글 삭제"));
        } catch (APIError e) {
            return new Response<>(new ErrorResponse(e.getCode(), e.getMessage()));
        }

    }




}
