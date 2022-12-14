package com.there.src.search;

import com.there.config.BaseException;
import com.there.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;


@Service
@RequiredArgsConstructor
public class SearchService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchDao searchDao;
    private final SearchProvider searchProvider;


    // 최근 검색어 삭제
    public void deleteRecentSearch(int userIdx, int searchIdx) throws BaseException {

        if(searchProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        // 해당하는 유저의 검색 기록인지
        if(searchProvider.checkUserSearch(userIdx, searchIdx) == 0){
            throw new BaseException(USERS_INVALID_ID);
        }

        try{
            searchDao.deleteRecentSearch(userIdx, searchIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 최근 검색어 모두 삭제
    public void deleteAllRecentSearch(int userIdx) throws BaseException {

        if(searchProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{

            // 검색 기록이 존재하지 않을 때까지 삭제
            while(searchProvider.checkUserSearchExist(userIdx) == 1){
                searchDao.deleteAllRecentSearch(userIdx);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


}