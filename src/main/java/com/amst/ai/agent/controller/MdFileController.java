package com.amst.ai.agent.controller;


import com.amst.ai.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.amst.ai.agent.model.entity.MdFile;
import com.amst.ai.agent.service.MdFileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 文件(MdFile)表控制层
 *
 * @author makejava
 * @since 2025-09-24 15:38:09
 */
@RestController
@RequestMapping("mdFile")
public class MdFileController {
    /**
     * 服务对象
     */
    @Resource
    private MdFileService mdFileService;

    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param mdFile 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result<Page<MdFile>> selectAll(Page<MdFile> page, MdFile mdFile) {
        return Result.ok(this.mdFileService.page(page, new QueryWrapper<>(mdFile)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<MdFile> selectOne(@PathVariable Serializable id) {
        return Result.ok(this.mdFileService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param mdFile 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result<Boolean> insert(@RequestBody MdFile mdFile) {
        return Result.ok(this.mdFileService.save(mdFile));
    }

    /**
     * 修改数据
     *
     * @param mdFile 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody MdFile mdFile) {
        return Result.ok(this.mdFileService.updateById(mdFile));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.ok(this.mdFileService.removeByIds(idList));
    }
}

