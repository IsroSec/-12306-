package com.jiawa.train.member.service;

import cn.hutool.core.collection.CollectionUtil;
import com.jiawa.train.member.domain.Member;
import com.jiawa.train.member.domain.MemberExample;
import com.jiawa.train.member.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: MemberService
 * Package: com.jiawa.train.member.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/23 5:18
 * @Version 1.0
 */
@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public Long register(String mobile) {
        //example是查询条件
        MemberExample memberExample = new MemberExample();
        //createCriteria相当于where条件
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isNotEmpty(members)){
            throw new RuntimeException("手机号已存在");
        }
        //创建一个会员实例
        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
