package com.kgc.kmall.service;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.MemberReceiveAddress;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-15 16:16
 */
public interface MemberService {
    List<Member> memberList();
    Member login(Member member);
    void addUserToken(String token, Long memberId);

    Member checkOauthUser(Long sourceUid);

    void addOauthUser(Member umsMember);
    List<MemberReceiveAddress> getReceiveAddressByMemberId(Long memberId);
}
