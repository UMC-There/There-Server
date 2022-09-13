package com.there.src.follow;


import com.there.src.follow.model.GetFollowerListRes;
import com.there.src.follow.model.GetFollowingListRes;
import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FollowService followService;
    private final JwtService jwtService;

    /**
     * 팔로우 API
     * [PATCH] follow/users/:userIdx/:followeeIdx
     */
    @ApiOperation(value = "팔로우 API", notes = "userIdx가 followeeIdx를 follow" +
            "ex) userIdx= 30(==나, 로그인한 유저), followeeIdx = 52 --> 30 유저가 52를 팔로우")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    // 팔로우
    @ResponseBody
    @PatchMapping("/users/{userIdx}/{followeeIdx}")
    public BaseResponse<String> follow
    (@PathVariable("userIdx") int userIdx, @PathVariable("followeeIdx")int followeeIdx) throws BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
        try {
            followService.follow(userIdx, followeeIdx);
            String result = "팔로우 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 언팔로우 API
     * [PATCH] follow/:followIdx
     */
    @ApiOperation(value = "언팔로우 API", notes = "PathVariable로 들어온 followIdx의 팔로우 status를 'INACTIVE'로 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    // 언팔로우
    @ResponseBody
    @PatchMapping("/{followIdx}/status")
    public BaseResponse<String> unfollow(@PathVariable("followIdx")int followIdx) {

        try {
            followService.unfollow(followIdx);
            String result = "팔로우 취소되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 내가 팔로우하는 유저 리스트 조회 API
     * [GET] follow/:userIdx/followingList
     */
    @ApiOperation(value = "내가 팔로우하는 유저 리스트 API", notes = "PathVariable로 들어온 userIdx가 팔로우한 유저 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{userIdx}/followingList")
    public BaseResponse<List<GetFollowerListRes>> getFollowerList
            (@PathVariable("userIdx") int userIdx) throws BaseException {
        List<GetFollowerListRes> getFollowerListRes = followService.FollowerList(userIdx);
        return new BaseResponse<>(getFollowerListRes);
    }

    /**
     * 나를 팔로우하는 유저 리스트 조회 API
     * [GET] follow/:userIdx/followerList
     */
    @ApiOperation(value = "나를 팔로우하는 유저 리스트 API", notes = "PathVariable로 들어온 userIdx를 팔로우하는 유저 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{userIdx}/followerList")
    public BaseResponse<List<GetFollowingListRes>> getFollowingList
            (@PathVariable("userIdx")int userIdx) throws BaseException {
        List<GetFollowingListRes> getFollowingListRes = followService.FollowingList(userIdx);
        return new BaseResponse<>(getFollowingListRes);
    }

}