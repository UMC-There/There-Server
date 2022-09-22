package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.artistStatement.model.GetArtistStatementRes;
import com.there.src.artistStatement.model.PatchArtistStatementReq;
import com.there.src.artistStatement.model.PostArtistStatementReq;
import com.there.src.artistStatement.model.PostArtistStatementRes;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.there.config.BaseResponseStatus.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/statements")
public class ArtistStatementController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtistStatementService artistStatementService;
    private final JwtService jwtService;


    /**
     * 작가노트 조회 API
     * [GET] /statements/users/:userIdx
     */
    @ApiOperation(value="작가노트 조회 API", notes="유저의 작가노트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    @ResponseBody
    @GetMapping("/users/{userIdx}")
    public BaseResponse<GetArtistStatementRes> getArtistStatement(@PathVariable("userIdx") int userIdx) {
        try {

            GetArtistStatementRes getArtistStatementRes = artistStatementService.retrieveStatement(userIdx);
            return new BaseResponse<>(getArtistStatementRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * 작가노트 작성 API
//     * [POST] /statements/users/:userIdx
//     */
//    @ApiOperation(value="작가노트 작성 API", notes="유저의 작가노트를 작성합니다.")
//    @ApiResponses({
//            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
//            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
//            @ApiResponse(code = 2110, message = "이미 작성된 작가노트가 있습니다."),
//            @ApiResponse(code = 2111, message = "자기소개 글자 수를 확인해주세요."),
//            @ApiResponse(code = 2112, message = "추구하는 작품 소개 글자 수를 확인해주세요."),
//            @ApiResponse(code = 2113, message = "연락처 글자 수를 확인해주세요."),
//            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
//    })
//    @ResponseBody
//    @PostMapping("/users/{userIdx}")
//    public BaseResponse<PostArtistStatementRes> createArtistStatement(@PathVariable("userIdx") int userIdx,
//                                                                      @RequestBody PostArtistStatementReq postArtistStatementReq) {
//        try {
//
//            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
//            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
//
//            if(postArtistStatementReq.getSelfIntroduction().length() > 300) {
//                return new BaseResponse<>(STATEMENTS_INVALID_SELFINTRO);
//            }
//
//            if(postArtistStatementReq.getWorkIntroduction().length() > 300) {
//                return new BaseResponse<>(STATEMENTS_INVALID_WORKINTRO);
//            }
//
//            if(postArtistStatementReq.getContact().length() > 50) {
//                return new BaseResponse<>(STATEMENTS_INVALID_CONTACT);
//            }
//            PostArtistStatementRes postArtistStatementRes = artistStatementService.createStatement(userIdx, postArtistStatementReq);
//            return new BaseResponse<>(postArtistStatementRes);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 작가노트 수정 API
     * [PATCH] /statements/users/:userIdx
     */
    @ApiOperation(value="작가노트 수정 API", notes="유저의 작가노트를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2111, message = "자기소개 글자 수를 확인해주세요."),
            @ApiResponse(code = 2112, message = "추구하는 작품 소개 글자 수를 확인해주세요."),
            @ApiResponse(code = 2113, message = "연락처 글자 수를 확인해주세요."),
            @ApiResponse(code = 2114, message = "작성된 작가노트가 없습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4121, message = "작가노트 수정에 실패하였습니다."),
    })
    @ResponseBody
    @PatchMapping("/users/{userIdx}")
    public BaseResponse<String> modifyArtistStatement(@PathVariable("userIdx") int userIdx,
                                                      @RequestBody PatchArtistStatementReq patchArtistStatementReq) {
        try {

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            if(patchArtistStatementReq.getSelfIntroduction() != null) {
                if (patchArtistStatementReq.getSelfIntroduction().length() > 300) {
                    return new BaseResponse<>(STATEMENTS_INVALID_SELFINTRO);
                }
            }

            if(patchArtistStatementReq.getWorkIntroduction() != null) {
                if (patchArtistStatementReq.getWorkIntroduction().length() > 300) {
                    return new BaseResponse<>(STATEMENTS_INVALID_WORKINTRO);
                }
            }

            if(patchArtistStatementReq.getContact() != null) {
                if (patchArtistStatementReq.getContact().length() > 50) {
                    return new BaseResponse<>(STATEMENTS_INVALID_CONTACT);
                }
            }

            artistStatementService.modifyStatement(userIdx, patchArtistStatementReq);
            String result = "작가노트가 수정되었습니다.";

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 작가노트 삭제 API
     * [DELETE] /statements/users/:userIdx
     */
    @ApiOperation(value="작가노트 삭제 API", notes="유저의 작가노트를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2114, message = "작성된 작가노트가 없습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4122, message = "작가노트 삭제에 실패하였습니다."),
    })
    @ResponseBody
    @DeleteMapping("/users/{userIdx}")
    public BaseResponse<String> deleteArtistStatement(@PathVariable("userIdx") int userIdx) {
        try {

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            artistStatementService.deleteStatement(userIdx);
            String result = "작가노트가 삭제되었습니다.";

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}

