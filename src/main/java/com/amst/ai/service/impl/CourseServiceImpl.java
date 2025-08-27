package com.amst.ai.service.impl;
import com.amst.ai.model.entity.CourseEntity;
import com.amst.ai.mapper.CourseMapper;
import com.amst.ai.service.CourseService;
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

