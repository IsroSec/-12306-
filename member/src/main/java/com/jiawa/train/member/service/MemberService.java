package com.jiawa.train.member.service;

import cn.hutool.core.collection.CollectionUtil;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.member.domain.Member;
import com.jiawa.train.member.domain.MemberExample;
import com.jiawa.train.member.mapper.MemberMapper;
import com.jiawa.train.member.req.MemberRegisterReq;
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

    public long register(MemberRegisterReq registerReq) {
        //example是查询条件
        MemberExample memberExample = new MemberExample();
        //createCriteria相当于where条件
        memberExample.createCriteria().andMobileEqualTo(registerReq.getMobile());
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isNotEmpty(members)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        //创建一个会员实例
        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(registerReq.getMobile());
        memberMapper.insert(member);
        return member.getId();
    }
}
