package com.there.src.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Repository
public class S3Dao {
    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * 히스토리 사진 업로드
     */
    public void uploadHistoryPicture(String imgPath, int historyIdx) {
        String uploadHistoryPictureQuery = "insert into historyPicture(imgUrl, historyIdx) VALUES (?,?);";
        Object[] uploadHistoryPictureParams = new Object[]{imgPath, historyIdx};
        this.jdbcTemplate.update(uploadHistoryPictureQuery, uploadHistoryPictureParams);
    }


    /**
     * 히스토리 사진 삭제 (사진 일괄 삭제)
     */
    public void delHistoryAllPicture(int historyIdx) {
        String delHistoryPictureQuery = "delete from historyPicture where historyIdx=?;";
        this.jdbcTemplate.update(delHistoryPictureQuery, historyIdx);
    }

    /**
     * 유저 프로필 사진 업로드
     */
    public void uploadUserProfileImg(String imgPath, int userIdx){
        String uploadUserProfileImgQuery = "update User SET profileImgUrl = ? where userIdx = ?;";
        Object[] uploadUserProfileImgParams = new Object[]{imgPath, userIdx};
        this.jdbcTemplate.update(uploadUserProfileImgQuery, uploadUserProfileImgParams);
    }

    /**
     * 유저 프로필 사진 삭제
     */
    public void delUserProfileImg(int userIdx) {
        String delHistoryPictureQuery = "update User SET profileImgUrl = null where userIdx = ?;";
        this.jdbcTemplate.update(delHistoryPictureQuery, userIdx);
    }

    /**
     * 게시물 사진 업로드
     */
    public void uploadPostImg(String imgPath, int postIdx){
        String uploadPostImgUrlQuery = "update Post SET imgUrl = ? where postIdx = ?;";
        Object[] uploadPostImgUrlParams = new Object[]{imgPath, postIdx};
        this.jdbcTemplate.update(uploadPostImgUrlQuery, uploadPostImgUrlParams);
    }

    /**
     * 포트폴리오 이미지 업로드
     */
    public void uploadPortfolioImg(String imgPath, int portfolioIdx) {
        String uploadPostImgUrlQuery = "update Portfolio SET ImgUrl = ? where portfolioIdx = ?;";
        Object[] uploadPostImgUrlParams = new Object[]{imgPath, portfolioIdx};
        this.jdbcTemplate.update(uploadPostImgUrlQuery, uploadPostImgUrlParams);
    }
}

