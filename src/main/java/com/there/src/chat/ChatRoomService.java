package com.there.src.chat;

import com.there.config.*;
import com.there.src.chat.model.GetRoomListRes;
import com.there.src.chat.model.PostChatRoomRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class ChatRoomService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatRoomDao chatRoomDao;

    @Autowired
    public ChatRoomService(ChatRoomDao chatRoomDao) {
        this.chatRoomDao = chatRoomDao;
    }

    /**
     * 채팅방 목록 조회
     */
    public List<GetRoomListRes> retrieveChatRoom(int userIdx) {

        List<GetRoomListRes> getRoomInfoList = chatRoomDao.selectChatRoomList(userIdx);

        return getRoomInfoList;
    }

    /**
     * 채팅방 생성
     */
    public PostChatRoomRes createRoom(int senderIdx, int receiverIdx) throws BaseException {

        try {
            int roodIdx = chatRoomDao.selectRoomIdx(senderIdx, receiverIdx);
            return new PostChatRoomRes(roodIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 채팅방 삭제
     */
    public void deleteChatRoom(int roomIdx) throws BaseException {
        try {
            int result = chatRoomDao.deleteChatRoom(roomIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_CHATROOM); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
