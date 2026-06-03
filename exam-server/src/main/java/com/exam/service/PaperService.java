package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.dto.PaperDTO;
import com.exam.entity.Paper;
import com.exam.entity.PaperQuestion;
import com.exam.entity.Question;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.PaperQuestionMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    public PageResult<Paper> listPage(Integer pageNum, Integer pageSize, String keyword, String status) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Paper::getPaperName, keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Paper::getStatus, status);
        }
        wrapper.orderByDesc(Paper::getCreateTime);
        Page<Paper> result = paperMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    public Paper getById(Long id) {
        return paperMapper.selectById(id);
    }

    @Transactional
    public void create(PaperDTO dto) {
        Paper paper = new Paper();
        paper.setPaperName(dto.getPaperName());
        paper.setDescription(dto.getDescription());
        paper.setDuration(dto.getDuration());
        paper.setTotalScore(dto.getTotalScore());
        paper.setPassScore(dto.getPassScore());
        paper.setStatus("DRAFT");
        paper.setTeacherId(UserContextUtil.getCurrentUserId());
        paperMapper.insert(paper);

        if (dto.getQuestions() != null) {
            saveQuestions(paper.getId(), dto);
        }
    }

    @Transactional
    public void update(Long id, PaperDTO dto) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) throw new IllegalArgumentException("试卷不存在");
        paper.setPaperName(dto.getPaperName());
        paper.setDescription(dto.getDescription());
        paper.setDuration(dto.getDuration());
        paper.setTotalScore(dto.getTotalScore());
        paper.setPassScore(dto.getPassScore());
        paperMapper.updateById(paper);

        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, id));
        if (dto.getQuestions() != null) {
            saveQuestions(id, dto);
        }
    }

    public void publish(Long id) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) throw new IllegalArgumentException("试卷不存在");
        Long count = paperQuestionMapper.selectCount(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, id));
        if (count == 0) throw new IllegalArgumentException("试卷中没有试题，无法发布");
        paper.setStatus("PUBLISHED");
        paperMapper.updateById(paper);
    }

    public void close(Long id) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) throw new IllegalArgumentException("试卷不存在");
        paper.setStatus("CLOSED");
        paperMapper.updateById(paper);
    }

    public void delete(Long id) {
        paperMapper.deleteById(id);
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, id));
    }

    public java.util.List<Question> getQuestionsByPaperId(Long paperId) {
        java.util.List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, paperId)
                        .orderByAsc(PaperQuestion::getSortOrder));
        java.util.List<Question> questions = new java.util.ArrayList<>();
        for (PaperQuestion pq : pqs) {
            Question q = questionMapper.selectById(pq.getQuestionId());
            if (q != null) {
                q.setScore(pq.getScore());
                questions.add(q);
            }
        }
        return questions;
    }

    private void saveQuestions(Long paperId, PaperDTO dto) {
        AtomicInteger order = new AtomicInteger(0);
        for (PaperDTO.QuestionItem item : dto.getQuestions()) {
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paperId);
            pq.setQuestionId(item.getQuestionId());
            pq.setScore(item.getScore() != null ? item.getScore() : 5);
            pq.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : order.incrementAndGet());
            paperQuestionMapper.insert(pq);
        }
    }

    public long count() {
        return paperMapper.selectCount(null);
    }
}
