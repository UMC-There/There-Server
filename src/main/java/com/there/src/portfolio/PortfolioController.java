package com.there.src.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.there.config.*;
import com.there.src.portfolio.model.*;
import com.there.src.s3.S3Service;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.config.BaseResponseStatus.EMPTY_IMGURL;
import static com.there.config.BaseResponseStatus.EMPTY_TITLE;

@Api
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtService jwtService;
    private final PortfolioService portfolioService;
    private final S3Service s3Service;

    @Autowired
    public PortfolioController(JwtService jwtService, PortfolioService portfolioService, S3Service s3Service) {
        this.jwtService = jwtService;
        this.portfolioService = portfolioService;
        this.s3Service = s3Service;
    }

    @ApiOperation(value="Portfolio 생성 API", notes="포트폴리오 제목, 대표 사진 반드시 필요")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public BaseResponse<PostPortfolioRes> createPortfolios
            (@RequestParam("jsonList") String jsonList,
             @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PostPortfolioReq postPortfolioReq = objectMapper.readValue(jsonList, new TypeReference<>() {});

        try {
            int userIdx = jwtService.getUserIdx1(jwtService.getJwt());

            if (postPortfolioReq.getTitle() == null) return new BaseResponse<>(EMPTY_TITLE);
            if (MultipartFiles == null) return new BaseResponse<>(EMPTY_IMGURL);

            PostPortfolioRes Portfolio = portfolioService.createPortfolios(userIdx, postPortfolioReq, MultipartFiles);
            return new BaseResponse<>(Portfolio);

        } catch (BaseException exception) {

            return new BaseResponse<>(exception.getStatus());

        }
    }

    @ApiOperation(value="Portfolio 내 Post 추가 API", notes="반드시 1개 이상의 포스트 선택")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping(value = "/{portfolioIdx}/content", consumes = {"multipart/form-data"})
    public BaseResponse<String> createPostInPortfolio
            (@PathVariable("portfolioIdx")int portfolioIdx, @RequestParam("jsonList") String jsonList) throws BaseException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PostPostInPortfolioReq postPostInPortfolioReq = objectMapper.readValue(jsonList, new TypeReference<>() {});

        portfolioService.createPostInPortfolio(portfolioIdx, postPostInPortfolioReq);
        String result = "포스트 추가를 하였습니다.";

        return new BaseResponse<>(result);
    }


    @ApiOperation(value="Portfolio 리스트 조회 API", notes="포트폴리오 클릭 시 해당 유저의 포트폴리오 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/user/{userIdx}")
    public BaseResponse<List<GetPortfolioListRes>> getPortfolioList(@PathVariable("userIdx") int userIdx) {
    
        try {

            List<GetPortfolioListRes> PortfolioListRes = portfolioService.getPortfolioList(userIdx);

            return new BaseResponse<>(PortfolioListRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ApiOperation(value="Portfolio 내 Post 조회 API", notes="포트폴리오 제목 클릭 시 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/pf/{portfolioIdx}")
    public BaseResponse<List<GetPortfolioRes>> getPortfolios (@PathVariable("portfolioIdx") int portfolioIdx) throws BaseException {
        List<GetPortfolioRes> PortfolioListRes = portfolioService.getPortfolios(portfolioIdx);
        return new BaseResponse<>(PortfolioListRes);

    }

    @ApiOperation(value="Portfolio 수정 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/change/{portfolioIdx}")
    public BaseResponse<String> ModifyPortfolio
            (@PathVariable("portfolioIdx")int portfolioIdx, @RequestParam("jsonList") String jsonList,
             @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles) throws BaseException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PatchPortfolioReq patchPortfolioReq = objectMapper.readValue(jsonList, new TypeReference<>() {});

        String result = portfolioService.ModifyPortfolio(portfolioIdx, patchPortfolioReq, MultipartFiles);
        return new BaseResponse<>(result);

    }

    @ApiOperation(value="Portfolio 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/pf/{portfolioIdx}")
    public BaseResponse<String> deletePortfolio (@PathVariable("portfolioIdx") int portfolioIdx) throws BaseException {

        portfolioService.deletePortfolio(portfolioIdx);
        String result = "포트폴리오 삭제를 성공했습니다.";
        return new BaseResponse<>(result);

    }

    @ApiOperation(value="Portfolio 내 Post 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/content/{contentIdx}")
    public BaseResponse<String> deletePostInPortfolio (@PathVariable("contentIdx") int contentIdx) throws BaseException {

        portfolioService.deletePostInPortfolio(contentIdx);
        String result = "포트폴리오 내 게시물 삭제를 성공했습니다.";
        return new BaseResponse<>(result);

    }

}
