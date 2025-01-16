package com.jiawa.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.domain.Ticket;
import com.jiawa.train.member.domain.TicketExample;
import com.jiawa.train.member.mapper.TicketMapper;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.req.TicketSaveReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TicketService
 * Package: com.jiawa.train.member.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketMapper ticketMapper;
    public void save(TicketSaveReq ticketSaveReq) {
        Ticket ticket = BeanUtil.copyProperties(ticketSaveReq, Ticket.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(ticket.getId())) {
//设置一下memberId
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        }else {
            ticket.setUpdateTime(now);
            ticketMapper.updateByPrimaryKey(ticket);
        }
    }

    public PageResp<TicketQueryResp> queryList(TicketQueryReq ticketQueryReq) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("id desc");
        TicketExample.Criteria criteria = ticketExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", ticketQueryReq.getPage());
        LOG.info("每页条数：{}", ticketQueryReq.getSize());
        PageHelper.startPage(ticketQueryReq.getPage(), ticketQueryReq.getSize());
        List<Ticket> tickets = ticketMapper.selectByExample(ticketExample);
        PageInfo<Ticket> ticketPageInfo = new PageInfo<>(tickets);

        LOG.info("总行数：{}", ticketPageInfo.getTotal());
        LOG.info("总页数：{}", ticketPageInfo.getPages());
        List<TicketQueryResp> list = BeanUtil.copyToList(tickets, TicketQueryResp.class);
        PageResp<TicketQueryResp> ticketQueryRespPageResp = new PageResp<>();
        ticketQueryRespPageResp.setTotal(ticketPageInfo.getTotal());
        ticketQueryRespPageResp.setList(list);
        return ticketQueryRespPageResp;
    }
    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
