package com.there.src.like;

import com.there.config.*;
import com.there.src.like.model.DeleteLikeReq;
import com.there.src.like.model.GetLikeRes;
import com.there.src.like.model.PostLikeReq;
import com.there.src.like.model.PostLikeRes;
import com.there.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LikeService {

    private final LikeDao likeDao;
    private final JwtService jwtService;

    @Autowired
    public LikeService(LikeDao likeDao,JwtService jwtService) {
        this.likeDao = likeDao;
        this.jwtService = jwtService;
    }

    // 좋아요 및 감정표현 조회
    public List<GetLikeRes> retrieveLikes(int postIdx) throws BaseException {
        try {
            List<GetLikeRes> getLikes = likeDao.selectLikes(postIdx);
            return getLikes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 좋아요 및 감정표현 생성
    public PostLikeRes createLikes(int userIdx, PostLikeReq postLikeReq) throws BaseException {
        try {
            int likesIdx = likeDao.createLikes(userIdx, postLikeReq);
            return new PostLikeRes(likesIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 좋아요 및 감정표현 수정
    public void updateLikes(int userIdx, int postIdx, int emotion) throws BaseException {
        try {
            int result = likeDao.updateLikes(userIdx, postIdx, emotion);
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 좋아요 및 감정표현 삭제
    public void deleteLikes(int likesIdx,DeleteLikeReq deleteLikeReq) throws BaseException {
        try {
            int result = likeDao.deleteLikes(likesIdx, deleteLikeReq);
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
