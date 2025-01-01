package com.jiawa.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.${module}.domain.${Domain};
import com.jiawa.train.${module}.domain.${Domain}Example;
import com.jiawa.train.${module}.mapper.${Domain}Mapper;
import com.jiawa.train.${module}.req.${Domain}QueryReq;
import com.jiawa.train.${module}.req.${Domain}SaveReq;
import com.jiawa.train.${module}.resp.${Domain}QueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: ${Domain}Service
 * Package: com.jiawa.train.${module}.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class ${Domain}Service {

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);

    @Autowired
    private ${Domain}Mapper ${domain}Mapper;
    public void save(${Domain}SaveReq ${domain}SaveReq) {
        ${Domain} ${domain} = BeanUtil.copyProperties(${domain}SaveReq, ${Domain}.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(${domain}.getId())) {
//设置一下memberId
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        }else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }
    }

    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq ${domain}QueryReq) {
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.setOrderByClause("id desc");
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", ${domain}QueryReq.getPage());
        LOG.info("每页条数：{}", ${domain}QueryReq.getSize());
        PageHelper.startPage(${domain}QueryReq.getPage(), ${domain}QueryReq.getSize());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(${domain}Example);
        PageInfo<${Domain}> ${domain}PageInfo = new PageInfo<>(${domain}s);

        LOG.info("总行数：{}", ${domain}PageInfo.getTotal());
        LOG.info("总页数：{}", ${domain}PageInfo.getPages());
        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}s, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> ${domain}QueryRespPageResp = new PageResp<>();
        ${domain}QueryRespPageResp.setTotal(${domain}PageInfo.getTotal());
        ${domain}QueryRespPageResp.setList(list);
        return ${domain}QueryRespPageResp;
    }
    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
