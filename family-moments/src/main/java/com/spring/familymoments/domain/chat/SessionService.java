package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponseStatus;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.spring.familymoments.config.BaseResponseStatus.minnie_FAMILY_INVALID_USER;
import static com.spring.familymoments.domain.chat.ChatRedisPrefix.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisService redisService;
    private final UserFamilyRepository userFamilyRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    // 연결
    @Transactional
    public void connect(String sessionId, User user) {
        saveSessionInfo(sessionId, user.getUuid());

        List<UserFamily> userFamilyList = userFamilyRepository.findAllActiveUserFamilyByUser(user);

        for(UserFamily userFamily : userFamilyList) {
            Family family = userFamily.getFamilyId();

            // offline 세션에서 제거
            redisService.removeMember(FAMILY_OFF.value + family.getFamilyId(), user.getUuid());

            // unsub 세션에 추가
            redisService.addValues(FAMILY_UNSUB.value + family.getFamilyId(), user.getUuid());
        }
    }

    // 연결 해제
    @Transactional
    public void disconnect(String sessionId) {
        String uuid = redisService.getValues(SESSION_ID.value + sessionId);
        User user = userRepository.findUserByUuid(uuid).orElseThrow(() -> new BaseException(BaseResponseStatus.FIND_FAIL_USER));

        // userID를 바탕으로 unsub 중인 내역이 있다면 offline으로 변경
        List<UserFamily> userFamilyList = userFamilyRepository.findAllActiveUserFamilyByUser(user);

        for(UserFamily userFamily : userFamilyList) {
            Family family = userFamily.getFamilyId();

            Set<String> members = redisService.getMembers(FAMILY_UNSUB.value + family.getFamilyId());
            boolean isSubscribing = !members.contains(uuid);

            // unsub 세션에서 제거
            redisService.removeMember(FAMILY_UNSUB.value + family.getFamilyId(), uuid);

            // sub 중인 채팅방이 있다면 lastAccessedTime 갱신
            if(isSubscribing) {
                userFamily.updateLastAccessedTime(LocalDateTime.now());
            }

            // offline 세션에 추가
            redisService.addValues(FAMILY_OFF.value + family.getFamilyId(), uuid);
        }

        // sessionId:userID 삭제
        redisService.deleteValues(SESSION_ID.value + sessionId);
    }


    // 가족 방 구독
    @Transactional(readOnly = true)
    public void subscribeFamily(User user, Long familyId) {
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(BaseResponseStatus.FIND_FAIL_FAMILY));
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyAndUser(family, user)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.minnie_FAMILY_INVALID_USER));

        // unsub 세션에서 제거
        redisService.removeMember(FAMILY_UNSUB.value + familyId, user.getUuid());
    }

    // 가족 방 구독 해제
    @Transactional
    public void unsubscribeFamily(User user, Long familyId) {
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(BaseResponseStatus.FIND_FAIL_FAMILY));
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyAndUser(family, user)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.minnie_FAMILY_INVALID_USER));
        
        unsubscribeFamily(userFamily);
    }

    // 가족 방 구독 해제
    @Transactional
    public void unsubscribeFamily(UserFamily userFamily) {
        // 마지막 접속 시간 갱신
        userFamily.updateLastAccessedTime(LocalDateTime.now());

        // unsub 세션에 추가
        redisService.addValues(FAMILY_UNSUB.value + userFamily.getFamilyId().getFamilyId(), userFamily.getUserId().getUuid());
    }

    // 접속한 유저의 세션 정보 저장
    public void saveSessionInfo(String sessionId, String userId) {
        redisService.setValues(SESSION_ID.value + sessionId, userId);
    }

    // 마지막 접속 시간 조회 TODO: 차후 삭제
    @Transactional
    public LocalDateTime getLastAccessedTime(User user, Family family) {
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyAndUser(family, user)
                .orElseThrow(()-> new BaseException(minnie_FAMILY_INVALID_USER));

        return userFamily.getLastAccessedTime();
    }

}
