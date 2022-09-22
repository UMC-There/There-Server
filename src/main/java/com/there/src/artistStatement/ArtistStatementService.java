package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.src.artistStatement.model.GetArtistStatementRes;
import com.there.src.artistStatement.model.PatchArtistStatementReq;
import com.there.src.artistStatement.model.PostArtistStatementReq;
import com.there.src.artistStatement.model.PostArtistStatementRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ArtistStatementService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtistStatementDao artistStatementDao;

    // 작가노트 조회
    public GetArtistStatementRes retrieveStatement(int userIdx) throws BaseException {
        try {
            if(checkStatementExist(userIdx) == 0) {
                createDefaultStatement(userIdx);
            }

            GetArtistStatementRes getArtistStatementRes = artistStatementDao.selectStatement(userIdx);

            return getArtistStatementRes;

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 작가노트 작성
//    public PostArtistStatementRes createStatement(int userIdx, PostArtistStatementReq postArtistStatementReq)
//            throws BaseException {
//
//            if(artistStatementProvider.checkStatementExist(userIdx) == 1){
//                throw new BaseException(STATEMENTS_EXIST);
//            }
//
//            try{
//
//                int statementIdx = artistStatementDao.insertStatement(userIdx, postArtistStatementReq);
//                return new PostArtistStatementRes(statementIdx);
//            }
//            catch (Exception exception) {
//                throw new BaseException(DATABASE_ERROR);
//
//            }
//    }

    // 작가노트 수정
    public void modifyStatement(int userIdx, PatchArtistStatementReq patchArtistStatementReq)
        throws BaseException {

        if(checkStatementExist(userIdx) == 0) {
            throw new BaseException(STATEMENTS_EMPTY);
        }

        try {
            int result = 0;

            // 1. 자기소개 수정
            if(patchArtistStatementReq.getSelfIntroduction() != null) {
                result = artistStatementDao.updateStatementSelfIntro(userIdx, patchArtistStatementReq);
            }
            // 2.추구하는 작품 소개 수정
            if(patchArtistStatementReq.getWorkIntroduction() != null) {
                result = artistStatementDao.updateStatementWorkIntro(userIdx, patchArtistStatementReq);
            }
            // 3. 연락처 수정
            if(patchArtistStatementReq.getContact() != null) {
                result = artistStatementDao.updateStatementContact(userIdx, patchArtistStatementReq);
            }

            if(result == 0) {
                throw new BaseException(UPDATE_FAIL_STATEMENT);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 작가노트 삭제
    public void deleteStatement(int userIdx) throws BaseException{

        if(checkStatementExist(userIdx) == 0) {
            throw new BaseException(STATEMENTS_EMPTY);
        }

        try {

            int result = artistStatementDao.deleteStatement(userIdx);

            if(result == 0) {
                throw new BaseException(DELETE_FAIL_STATEMENT);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 작가노트 체크
    public int checkStatementExist(int userIdx) throws BaseException {
        try {
            return artistStatementDao.checkStatementExist(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 작가노트 default 생성
    public int createDefaultStatement(int userIdx)
            throws BaseException {

            try{

                int statementIdx = artistStatementDao.insertDefaultStatement(userIdx);
                return statementIdx;
            }
            catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);

            }
    }

}