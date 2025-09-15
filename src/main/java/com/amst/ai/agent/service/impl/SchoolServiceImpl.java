package com.amst.ai.agent.service.impl;
import com.amst.ai.agent.model.entity.SchoolEntity;
import com.amst.ai.agent.service.SchoolService;

import com.amst.ai.agent.mapper.SchoolMapper;
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

