package com.jiawa.train.member.service;

import cn.hutool.core.collection.CollectionUtil;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.config.MemberApplication;
import com.jiawa.train.member.domain.Member;
import com.jiawa.train.member.domain.MemberExample;
import com.jiawa.train.member.mapper.MemberMapper;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class.getName());

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
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(registerReq.getMobile());
        memberMapper.insert(member);
        return member.getId();
    }

    public void sendCode(MemberSendCodeReq sendCodeReq) {
        String mobile = sendCodeReq.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isEmpty(members)){
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else {
            LOG.info("手机号存在，不插入记录");
        }
        // 生成验证码
        // String code = RandomUtil.randomString(4);
        String code = "8888";
        LOG.info("生成短信验证码：{}", code);

        // 保存短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        LOG.info("保存短信记录表");

        // 对接短信通道，发送短信
        LOG.info("对接短信通道");
    }

}
