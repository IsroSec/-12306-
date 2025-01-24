package com.jiawa.train.business.mapper.cust;

import java.util.Date;

/**
 * ClassName: SkTokenMapperCust
 * Package: com.jiawa.train.business.mapper.cust
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/24 20:43
 * @Version 1.0
 */
public interface SkTokenMapperCust {
    int decrease(Date date, String trainCode);
}
