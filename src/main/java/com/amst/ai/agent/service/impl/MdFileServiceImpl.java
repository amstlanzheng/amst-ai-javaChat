package com.amst.ai.agent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.amst.ai.agent.mapper.MdFileMapper;
import com.amst.ai.agent.model.entity.MdFile;
import com.amst.ai.agent.service.MdFileService;
import org.springframework.stereotype.Service;

/**
 * 文件(MdFile)表服务实现类
 *
 * @author makejava
 * @since 2025-09-24 15:38:15
 */
@Service("mdFileService")
public class MdFileServiceImpl extends ServiceImpl<MdFileMapper, MdFile> implements MdFileService {

}

