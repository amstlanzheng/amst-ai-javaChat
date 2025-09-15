package com.amst.ai.agent.service.impl;
import com.amst.ai.agent.model.entity.CourseEntity;
import com.amst.ai.agent.mapper.CourseMapper;
import com.amst.ai.agent.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 学科表(Course)表服务实现类
 *
 * @author amstlan
 * @since 2025-08-15 11:26:05
 */
@Service("courseService")
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseEntity> implements CourseService {

}

