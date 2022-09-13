package com.there.src.portfolio;

import com.there.config.*;
import com.there.src.portfolio.model.*;
import com.there.src.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class PortfolioService {

    private final PortfolioDao portfolioDao;
    private final S3Service s3Service;

    @Autowired
    public PortfolioService(PortfolioDao portfolioDao, S3Service s3Service) {
        this.portfolioDao = portfolioDao;
        this.s3Service = s3Service;
    }

    /**
     * Portfolio List 조회
     * @param userIdx
     * @return
     * @throws BaseException
     */
    public List<GetPortfolioListRes> getPortfolioList(int userIdx) throws BaseException {
        try {
            List<GetPortfolioListRes> getPortfolioListRes = portfolioDao.getPortfolioList(userIdx);
            return getPortfolioListRes;
        } catch (Exception exception) {
            throw new BaseException(GET_FAIL_LIST);
        }
    }

    /**
     * Portfolio 조회
     * @param portfolioIdx
     * @return
     * @throws BaseException
     */
    public List<GetPortfolioRes> getPortfolios(int portfolioIdx) throws BaseException {
        try {
            List<GetPortfolioRes> getPortfolioRes = portfolioDao.getPortfolios(portfolioIdx);
            return getPortfolioRes;
        } catch (Exception exception) {
            throw new BaseException(GET_FAIL_PORTFOLIO);
        }
    }

    /**
     *  Portfolio 생성
     */
    public PostPortfolioRes createPortfolios(int userIdx, PostPortfolioReq postPortfolioReq, List<MultipartFile> MultipartFiles) throws BaseException {

        // 이미지 1개로 제한
        if(MultipartFiles.size() > 1){
            throw new BaseException(EXCEEDED_IMGURL);
        }

        try {

            // 포트폴리오 생성
            int portfolioIdx = portfolioDao.createPortfolios(userIdx, postPortfolioReq);

            // s3 업로드
            String s3path = "portfolio/portfolioIdx : " + Integer.toString(portfolioIdx);
            String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

            // db 업로드
            s3Service.uploadPortfolioImg(imgPath, portfolioIdx);

            // 생성된 포트폴리오에 포스트 추가
            int [] postIdxList = postPortfolioReq.getPostIdx();
            if (postIdxList != null) {
                for (int postIdx : postIdxList) {
                    portfolioDao.createPostInPortfolio(portfolioIdx, postIdx);
                }
            }

            return new PostPortfolioRes(portfolioIdx);

        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_PORTFOLIO);
        }
    }

    /**
     * Portfolio 내 Post 추가
     */
    public void createPostInPortfolio
    (int portfolioIdx, PostPostInPortfolioReq postPostInPortfolioReq) throws BaseException {

        try {
            int [] postIdxList = postPostInPortfolioReq.getPostIdx();

            // 포스트를 선택하지 않았을 때 예외 발생
            if (postIdxList == null) throw new BaseException(EMPTY_POST);

            // 포트폴리오에 포스트 추가
            for (int postIdx : postIdxList) {
                portfolioDao.createPostInPortfolio(portfolioIdx, postIdx);
            }

        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_POSTINPORTFOLIO);
        }
    }

    public void deletePortfolio(int portfolioIdx) throws BaseException {
        try {
            int result = portfolioDao.deletePortfolio(portfolioIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_PORTFOLIO); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePostInPortfolio(int contentIdx) throws BaseException {
        try {
            int result = portfolioDao.deletePostInPortfolio(contentIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_POSTINPORTFOLIO); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String ModifyPortfolio
            (int portfolioIdx, PatchPortfolioReq patchPortfolioReq, List<MultipartFile> MultipartFiles) throws BaseException {

        String Title = patchPortfolioReq.getTitle();
        String result = null;

        try {
            if (Title != null && MultipartFiles != null) { // 모두 수정


                // s3 업로드 후 DB 수정 (기존 path 삭제 후 재 생성)
                s3Service.removeFolder("Post/postIdx : " + Integer.toString(portfolioIdx));

                String s3path = "portfolio/portfolioIdx : " + Integer.toString(portfolioIdx);
                String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                s3Service.uploadPortfolioImg(imgPath, portfolioIdx);

                // 제목 수정
                portfolioDao.updateTitle(portfolioIdx, Title);
                return result = "제목, 대표 사진을 수정 하였습니다.";
            }
            else if (MultipartFiles != null) { // 대표 사진 수정

                // s3 업로드 후 DB 수정 (기존 path 삭제 후 재 생성)
                s3Service.removeFolder("Post/postIdx : " + Integer.toString(portfolioIdx));

                String s3path = "portfolio/portfolioIdx : " + Integer.toString(portfolioIdx);
                String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                s3Service.uploadPortfolioImg(imgPath, portfolioIdx);

                return result = "대표 사진을 수정 하였습니다.";
            }
            else if(Title != null) {  // 제목 수정

                portfolioDao.updateTitle(portfolioIdx, Title);
                return result = "제목을 수정 하였습니다.";
            }
            else {
                throw new BaseException(FAIL_MODIFY);
            }
        }
       catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
