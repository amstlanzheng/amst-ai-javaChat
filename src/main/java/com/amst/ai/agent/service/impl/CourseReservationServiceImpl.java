package com.amst.ai.agent.service.impl;


import com.amst.ai.agent.model.entity.CourseReservationEntity;
import com.amst.ai.agent.mapper.CourseReservationMapper;
import com.amst.ai.agent.service.CourseReservationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (CourseReservation)表服务实现类
 *
 * @author amstlan
 * @since 2025-08-15 11:26:08
 */
@Service("courseReservationService")
public class CourseReservationServiceImpl extends ServiceImpl<CourseReservationMapper, CourseReservationEntity> implements CourseReservationService {

}

