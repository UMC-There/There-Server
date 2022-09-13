package com.there.src.comment;


import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.comment.model.*;
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

import java.util.List;

import static com.there.config.BaseResponseStatus.*;


@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentService commentService;
    private final JwtService jwtService;

    /**
     * 댓글 생성 API
     * [POST] comments/:postIdx
     */
    @ApiOperation(value = "댓글 생성 API")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/{postIdx}/{userIdx}")
    public BaseResponse<PostCommentRes> createComment
    (@PathVariable("postIdx") int postIdx, @PathVariable("userIdx") int userIdx,
     @RequestBody PostCommentReq postCommentReq) throws BaseException {
        try {

            if (!jwtService.validationToken(jwtService.getJwt())){
                throw new BaseException(ACCESS_TOKEN_ERROR);
            }

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postCommentReq.getContent() == null) return new BaseResponse<>(COMMENTS_EMPTY_CONTENT);

            PostCommentRes postCommentRes = commentService.createComment(postIdx, userIdx, postCommentReq);
            return new BaseResponse<>(postCommentRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 리스트 조회 API
     * [GET] comments/:commentIdx
     */
    @ApiOperation(value="댓글 리스트 조회 API", notes = "Pathvariable 로 들어온 postIdx의 달린 댓글 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{postIdx}")
    public BaseResponse<List<GetCommentListRes>> getCommentList(@PathVariable("postIdx") int postIdx)
            throws BaseException {

        List<GetCommentListRes> getCommentListResList = commentService.retrieveComment(postIdx);
        return new BaseResponse<>(getCommentListResList);

    }

    /**
     * 댓글 삭제 API
     * comments/:commentIdx/status
     */
    @ApiOperation(value = "댓글 삭제 API", notes ="실제 DB에서 삭제하지 않고 status를 'DELETED' 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/{commentIdx}/status")
    public BaseResponse<String> deleteComment(@PathVariable("commentIdx") int commentIdx)
            throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
        commentService.deleteComment(userIdxByJwt, commentIdx);

        String result = "댓글이 삭제되었습니다. ";
        return new BaseResponse<>(result);
    }

    /**
     * 댓글 수정 API
     * comments/change/:commentIdx
     */
    @ApiOperation(value="댓글 수정 API", notes ="입력 받은 내용으로 댓글 수정 ")
    @ApiResponses({
            @ApiResponse(code = 1000, message ="요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("change/{commentIdx}")
    public BaseResponse<String> updateComment(@PathVariable("commentIdx") int commentIdx,
    @RequestBody PatchCommentReq patchCommentReq) throws BaseException {

        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
        try {
            commentService.updateComment(commentIdx, patchCommentReq);
            String result = "댓글 수정을 완료하였습니다. ";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 대댓글 작성 API
     * comments/:postIdx/:userIdx/:commentIdx
     */
    @ApiOperation(value= "대댓글 작성 API", notes ="PathVariable로 받은 commentIdx의 대댓글 작성")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 1000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/{postIdx}/{userIdx}/{commentIdx}")
    public BaseResponse<PostReCommentRes> createReComment(@PathVariable("postIdx") int postIdx, @PathVariable("userIdx") int userIdx,
    @PathVariable("commentIdx") int commentIdx, @RequestBody PostReCommentReq postReCommentReq) throws BaseException{

        try {
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postReCommentReq.getContent() == null) return new BaseResponse<>(COMMENTS_EMPTY_CONTENT);

            PostReCommentRes postReCommentRes = commentService.createReComment(postIdx, userIdx, commentIdx, postReCommentReq);
            return new BaseResponse<>(postReCommentRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 대댓글 조회 API
     * comments/:postIdx/:commentIdx
     */
    @ApiOperation(value="대댓글 조회 API")
    @ApiResponses({
            @ApiResponse(code = 1000, message ="요청 성공"),
            @ApiResponse(code = 4000, message ="서버 에러")
    })
    @ResponseBody
    @GetMapping("/{postIdx}/{commentIdx}")
    public BaseResponse<List<GetReCommentListRes>> getReCommentList
            (@PathVariable("postIdx") int postIdx, @PathVariable("commentIdx") int commentIdx)
            throws BaseException {

        List<GetReCommentListRes> getReCommentListResList = commentService.ReComment(postIdx, commentIdx );
        return new BaseResponse<>(getReCommentListResList);

    }

    /**
     * 대댓글 삭제 API
     * comments/:commentIdx/RE/status
     */
    @ApiOperation(value="대댓글 삭제 API", notes = "db 기준 commentIdx 35는 commentIdx 33의 대댓글" +
            "PathVariable로 들어온 commentIdx는 삭제 할 대댓글인 35")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 1000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/{commentIdx}/Re/status")
    public BaseResponse<String> deleteReComment(@PathVariable("commentIdx") int commentIdx)
            throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
        commentService.deleteComment(userIdxByJwt, commentIdx);

        String result = "대댓글이 삭제되었습니다. ";
        return new BaseResponse<>(result);
    }

    /**
     * 대댓글 수정 API
     * comments/change/Re/:commentIdx
     */
    @ApiOperation(value="대댓글 수정 API", notes = "db 기준 commentIdx 35는 commentIdx 33의 대댓글" +
            "PathVariable로 들어온 commentIdx는 수정 할 대댓글인 35")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 1000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("change/Re/{commentIdx}")
    public BaseResponse<String> updateReComment(@PathVariable("commentIdx") int commentIdx,
    @RequestBody PatchCommentReq patchCommentReq) throws BaseException {

        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
        try {
            commentService.updateComment(commentIdx, patchCommentReq);
            String result = "대댓글 수정을 완료하였습니다. ";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

