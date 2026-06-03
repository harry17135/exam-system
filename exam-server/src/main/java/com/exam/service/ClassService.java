package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.entity.ClassEntity;
import com.exam.mapper.ClassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassMapper classMapper;

    public PageResult<ClassEntity> listPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<ClassEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClassEntity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ClassEntity::getClassName, keyword);
        }
        wrapper.orderByDesc(ClassEntity::getCreateTime);
        Page<ClassEntity> result = classMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    public List<ClassEntity> listAll() {
        return classMapper.selectList(null);
    }

    public void create(ClassEntity entity) {
        classMapper.insert(entity);
    }

    public void update(ClassEntity entity) {
        classMapper.updateById(entity);
    }

    public void delete(Long id) {
        classMapper.deleteById(id);
    }
}
