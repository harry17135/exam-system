package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import com.exam.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public PageResult<Question> listPage(Integer pageNum, Integer pageSize, String type, String keyword, Integer difficulty) {
        Page<Question> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Question::getType, type);
        }
        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getTitle, keyword);
        }
        wrapper.eq(Question::getStatus, 1);
        wrapper.orderByDesc(Question::getCreateTime);
        Page<Question> result = questionMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    public Question getById(Long id) {
        return questionMapper.selectById(id);
    }

    public void create(Question question) {
        question.setTeacherId(UserContextUtil.getCurrentUserId());
        question.setStatus(1);
        questionMapper.insert(question);
    }

    public void update(Question question) {
        questionMapper.updateById(question);
    }

    public void delete(Long id) {
        questionMapper.deleteById(id);
    }

    public long count() {
        return questionMapper.selectCount(null);
    }
}
