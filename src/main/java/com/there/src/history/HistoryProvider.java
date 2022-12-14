package com.there.src.history;

import com.there.config.*;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class HistoryProvider {

    private final HistoryDao historyDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    // 히스토리 조회
    public GetHistoryRes retrieveHistory(int historyIdx) throws BaseException {
        try {

            GetHistoryRes getHistoryRes = historyDao.selectHistory(historyIdx);
            return getHistoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 히스토리 리스트 조회
    public List<GetHistoryListRes> retrieveHistoryList(int postIdx) throws BaseException{
        try{
            List<GetHistoryListRes> getHistoryList = historyDao.selectHistoryList(postIdx);
            return getHistoryList;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return historyDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkHistoryExist(int historyIdx) throws BaseException{
        try{
            return historyDao.checkHistoryExist(historyIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserHistoryExist(int userIdx, int historyIdx) throws BaseException{
        try{
            return historyDao.checkUserHistoryExist(userIdx, historyIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserPostExist(int userIdx, int postIdx) throws BaseException{
        try{
            return historyDao.checkUserPostExist(userIdx, postIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
