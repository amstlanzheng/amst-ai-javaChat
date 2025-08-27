package com.amst.ai.service.impl;
import com.amst.ai.model.entity.SchoolEntity;
import com.amst.ai.service.SchoolService;

import com.amst.ai.mapper.SchoolMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 校区表(School)表服务实现类
 *
 * @author amstlan
 * @since 2025-08-15 11:26:09
 */
@Service("schoolService")
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, SchoolEntity> implements SchoolService {

}

