package com.amst.ai.agent.tools;

import com.amst.ai.agent.model.entity.CourseEntity;
import com.amst.ai.agent.model.entity.CourseReservationEntity;
import com.amst.ai.agent.model.entity.SchoolEntity;
import com.amst.ai.agent.model.query.CourseQuery;
import com.amst.ai.agent.service.CourseService;
import com.amst.ai.agent.service.SchoolService;
import com.amst.ai.agent.service.impl.CourseReservationServiceImpl;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CourseTools {

    private final CourseService courseService;

    private final SchoolService schoolService;

    private final CourseReservationServiceImpl courseReservationService;


    /**
     * 查询课程
     */
    @Tool(description = "根据条件查询课程")
    public List<CourseEntity> queryCourse(@ToolParam(required = false, description = "查询条件") CourseQuery query) {
        //判空
        if (query == null) {
            return courseService.list();
        }
        QueryChainWrapper<CourseEntity> wrapper = courseService.query()
                .eq(StringUtils.isNotBlank(query.getType()), "type", query.getType())
                .le(query.getEdu() != null, "edu", query.getEdu());

        if (!CollectionUtils.isEmpty(query.getSorts())){
            //排序
            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }

    /**
     * 查询学校
     */
    @Tool(description = "查询所有校区")
    private List<SchoolEntity> querySchool() {
        return schoolService.list();
    }

    /**
     * 新增预约单
     */
    @Tool(description = "新增生成预约单，返回预约单号")
    public Integer addReservation(@ToolParam(required = true, description = "预约课程") String course,
                                  @ToolParam(required = true, description = "预约校区") String school,
                                  @ToolParam (required = true, description = "学生姓名") String studentName,
                                  @ToolParam (required = true, description = "联系方式") String contactInfo,
                                  @ToolParam(required = false, description = "备注") String remark) {

        CourseReservationEntity courseReservationEntity = new CourseReservationEntity();
        courseReservationEntity.setCourse(course);
        courseReservationEntity.setSchool(school);
        courseReservationEntity.setStudentName(studentName);
        courseReservationEntity.setContactInfo(contactInfo);
        courseReservationEntity.setRemark(remark);
        courseReservationService.save(courseReservationEntity);


        return courseReservationEntity.getId();
    }

    /**
     * 根据用户名或者联系方式查询预约单
     */
    @Tool(description = "根据用户名或者联系方式查询预约单")
    public List<CourseReservationEntity> queryReservation(@ToolParam(required = true, description = "用户名或者联系方式") String usernameOrContactInfo) {
        return courseReservationService.query()
                .eq("studentName", usernameOrContactInfo)
                .or()
                .eq("contactInfo", usernameOrContactInfo)
                .list();
    }


    /**
     * 根据预约单号查询预约单信息
     */
    @Tool(description = "根据预约单号查询预约单信息")
    public CourseReservationEntity queryReservationById(@ToolParam(required = true, description = "预约单号") Integer id) {
        return courseReservationService.getById(id);
    }

}
