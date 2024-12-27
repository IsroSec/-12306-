package com.jiawa.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWTUtil;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.config.MemberApplication;
import com.jiawa.train.member.domain.Member;
import com.jiawa.train.member.domain.MemberExample;
import com.jiawa.train.member.mapper.MemberMapper;
import com.jiawa.train.member.req.MemberLoginReq;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.resp.MemberLoginResp;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        Member members = selectByMobile(registerReq.getMobile());
        if (ObjectUtil.isNotEmpty(members)){
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
        Member members = selectByMobile(mobile);
        if (ObjectUtil.isEmpty(members)){
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
    public MemberLoginResp Login(MemberLoginReq loginReq) {
        String mobile = loginReq.getMobile();
        Member members = selectByMobile(mobile);
        String code = loginReq.getCode();
        if (ObjectUtil.isEmpty(members)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        // 校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(members, MemberLoginResp.class);
        String saultKey="Jiawa12306";
        Map<String, Object> map = BeanUtil.beanToMap(memberLoginResp);
        String token = JWTUtil.createToken(map, saultKey.getBytes());
        memberLoginResp.setToken(token);
        return memberLoginResp;
    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isEmpty(members)){
            return null;
        }else {
            return members.get(0);
        }
    }

}
