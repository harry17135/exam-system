package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.dto.ArrangementDTO;
import com.exam.entity.ExamArrangement;
import com.exam.entity.Paper;
import com.exam.mapper.ExamArrangementMapper;
import com.exam.mapper.PaperMapper;
import com.exam.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ArrangementService {

    private final ExamArrangementMapper arrangementMapper;
    private final PaperMapper paperMapper;

    public PageResult<ExamArrangement> listPage(Integer pageNum, Integer pageSize) {
        Page<ExamArrangement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamArrangement> wrapper = new LambdaQueryWrapper<ExamArrangement>()
                .orderByDesc(ExamArrangement::getCreateTime);
        Page<ExamArrangement> result = arrangementMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    public void create(ArrangementDTO dto) {
        Paper paper = paperMapper.selectById(dto.getPaperId());
        if (paper == null) throw new IllegalArgumentException("试卷不存在");
        if (!"PUBLISHED".equals(paper.getStatus())) throw new IllegalArgumentException("试卷未发布，无法安排考试");

        ExamArrangement arrangement = new ExamArrangement();
        arrangement.setPaperId(dto.getPaperId());
        arrangement.setClassId(dto.getClassId());
        arrangement.setExamName(dto.getExamName() != null ? dto.getExamName() : paper.getPaperName());
        arrangement.setStartTime(LocalDateTime.parse(dto.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        arrangement.setEndTime(LocalDateTime.parse(dto.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        arrangement.setTeacherId(UserContextUtil.getCurrentUserId());
        arrangement.setStatus("PENDING");
        arrangementMapper.insert(arrangement);
    }

    public void update(Long id, ArrangementDTO dto) {
        ExamArrangement arrangement = arrangementMapper.selectById(id);
        if (arrangement == null) throw new IllegalArgumentException("考试安排不存在");
        arrangement.setPaperId(dto.getPaperId());
        arrangement.setClassId(dto.getClassId());
        arrangement.setExamName(dto.getExamName());
        arrangement.setStartTime(LocalDateTime.parse(dto.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        arrangement.setEndTime(LocalDateTime.parse(dto.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        arrangementMapper.updateById(arrangement);
    }

    public void delete(Long id) {
        arrangementMapper.deleteById(id);
    }
}
