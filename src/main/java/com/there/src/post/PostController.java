package com.there.src.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.post.model.*;
import com.there.src.s3.S3Service;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.there.config.BaseResponseStatus.*;

@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PostProvider postProvider;
    private final PostService postService;
    private final JwtService jwtService;


    /**
     * 게시글 조회 API
     * posts/:postIdx
     */

    @ApiOperation(value="게시글 조회 API", notes="PathVariable로 postIdx 받아와서 게시글 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{postIdx}")
    public BaseResponse<GetPostsRes> getPosts(@PathVariable("postIdx")int postIdx) {

        try{

            GetPostsRes getPostsRes = postProvider.retrievePosts(postIdx);
            return new BaseResponse<>(getPostsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
    * 게시글 생성 API
    * posts/users/:userIdx
    */
    @ApiOperation(value="게시글 생성 API", notes="Body 타입 : form-data<jsonList, images> jsonList - content, hashtag(5개까지)")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping(value = "/users/{userIdx}",consumes = {"multipart/form-data"})
    public BaseResponse<PostPostsRes> createPosts(@PathVariable("userIdx")int userIdx, @RequestParam("jsonList") String jsonList,
     @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PostPostsReq postPostsReq = objectMapper.readValue(jsonList, new TypeReference<>() {});

        try {

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (MultipartFiles == null) return new BaseResponse<>(EMPTY_IMGURL);
            if (postPostsReq.getContent() == null) return new BaseResponse<>(EMPTY_CONTENT);
            if (postPostsReq.getHashtag()!= null && postPostsReq.getHashtag().length > 5) return new BaseResponse<>(EXCEEDED_HASHTAG);

            PostPostsRes postPostsRes = postService.createPosts(userIdx, postPostsReq, MultipartFiles);
            return new BaseResponse<>(postPostsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시글 수정 API
     * posts/change/{postIdx}/users/:userIdx
     */
    @ApiOperation(value="게시글 수정 API", notes="Body 타입 : form-data<jsonList, images> jsonList - content, hashtag(5개까지), 이미지만 수정할 경우 jsonList값에 '{}' 넣기")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping(value = "change/{postIdx}/users/{userIdx}", consumes = {"multipart/form-data"})
    public BaseResponse<String> updatePosts(@PathVariable("postIdx")int postIdx, @PathVariable("userIdx")int userIdx,
                                            @RequestParam("jsonList") String jsonList, @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles)
            throws IOException, BaseException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PatchPostsReq patchPostsReq = objectMapper.readValue(jsonList, new TypeReference<>() {});
        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
        if (patchPostsReq.getHashtag() != null && patchPostsReq.getHashtag().length > 5) return new BaseResponse<>(EXCEEDED_HASHTAG);

        try {
            postService.updatePosts(postIdx, patchPostsReq, MultipartFiles);
            String result = "게시글 수정을 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시글 삭제 API
     * posts/{postIdx}/users/:userIdx
     */
    @ApiOperation(value="게시글 삭제 API", notes="실제 DB를 삭제하지 않고 status를 INACTIVE로 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("deletion/{postIdx}/users/{userIdx}")
    public BaseResponse<String> deletePosts
    (@PathVariable("postIdx") int postIdx, @PathVariable("userIdx") int userIdx) throws BaseException {

        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            postService.deletePosts(postIdx);
            String result = "게시글 삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 무작위(랜덤) 게시글 리스트 조회 API
     * /posts/random
     */
    @ApiOperation(value="무작위 게시글 리스트 조회 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("random")
    public BaseResponse<List<GetPostListRes>> getRandomPostList(){
        try {
            List<GetPostListRes> getPostListRes = postProvider.retrieveRandomPosts();
            return new BaseResponse<>(getPostListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 인기글, 내가 팔로우한 구독자의 게시글 리스트 조회 API
     * /posts/rankingAndfollowerPostList
     */
    @ApiOperation(value="인기글, 내가 팔로우한 구독자의 게시글 리스트 조회 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("rankingAndfollowerPostList")
    public BaseResponse<Map<String, List<GetPostListRes>>>getRankingAndFollowerPostList() {
        try {
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            Map<String, List<GetPostListRes>> getPostListRes = new HashMap<>();
            getPostListRes.put("인기글 리스트", postProvider.retrieveRankingPosts());
            getPostListRes.put("팔로우 게시글 리스트",postProvider.retrieveFollowerPosts(userIdxByJwt));


            return new BaseResponse<>(getPostListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}