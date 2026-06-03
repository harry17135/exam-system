package com.exam.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.dto.ExamStartDTO;
import com.exam.dto.ExamSubmitDTO;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.utils.UserContextUtil;
import com.exam.vo.ExamPaperVO;
import com.exam.vo.ExamRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final ExamArrangementMapper arrangementMapper;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    /**
     * 学生开始考试
     */
    @Transactional
    public ExamPaperVO startExam(ExamStartDTO dto) {
        Long studentId = UserContextUtil.getCurrentUserId();
        ExamArrangement arrangement = arrangementMapper.selectById(dto.getArrangementId());
        if (arrangement == null) throw new IllegalArgumentException("考试安排不存在");

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(arrangement.getStartTime())) throw new IllegalArgumentException("考试尚未开始");
        if (now.isAfter(arrangement.getEndTime())) throw new IllegalArgumentException("考试已结束");

        // 检查是否已有考试记录
        ExamRecord existRecord = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getArrangementId, dto.getArrangementId())
                        .eq(ExamRecord::getStudentId, studentId));
        if (existRecord != null) {
            if ("FINISHED".equals(existRecord.getStatus())) {
                throw new IllegalArgumentException("你已完成此考试，不能重复参加");
            }
            // 进行中的考试，允许继续
            return buildExamPaper(existRecord);
        }

        // 创建考试记录
        Paper paper = paperMapper.selectById(arrangement.getPaperId());
        ExamRecord record = new ExamRecord();
        record.setArrangementId(dto.getArrangementId());
        record.setPaperId(arrangement.getPaperId());
        record.setStudentId(studentId);
        record.setStatus("IN_PROGRESS");
        record.setStartTime(LocalDateTime.now());
        examRecordMapper.insert(record);

        return buildExamPaper(record);
    }

    private ExamPaperVO buildExamPaper(ExamRecord record) {
        Paper paper = paperMapper.selectById(record.getPaperId());
        ExamPaperVO vo = new ExamPaperVO();
        vo.setRecordId(record.getId());
        vo.setPaperId(paper.getId());
        vo.setPaperName(paper.getPaperName());
        vo.setDescription(paper.getDescription());
        vo.setDuration(paper.getDuration());
        vo.setTotalScore(paper.getTotalScore());
        vo.setPassScore(paper.getPassScore());

        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, paper.getId())
                        .orderByAsc(PaperQuestion::getSortOrder));
        List<ExamPaperVO.QuestionVO> questionVOs = new ArrayList<>();
        for (PaperQuestion pq : pqs) {
            Question q = questionMapper.selectById(pq.getQuestionId());
            if (q != null) {
                ExamPaperVO.QuestionVO qv = new ExamPaperVO.QuestionVO();
                qv.setQuestionId(q.getId());
                qv.setType(q.getType());
                qv.setTitle(q.getTitle());
                qv.setOptions(q.getOptions());
                qv.setScore(pq.getScore());
                qv.setSortOrder(pq.getSortOrder());
                questionVOs.add(qv);
            }
        }
        vo.setQuestions(questionVOs);
        return vo;
    }

    /**
     * 提交试卷
     */
    @Transactional
    public int submitExam(ExamSubmitDTO dto) {
        ExamRecord record = examRecordMapper.selectById(dto.getRecordId());
        if (record == null) throw new IllegalArgumentException("考试记录不存在");
        if ("FINISHED".equals(record.getStatus())) throw new IllegalArgumentException("已交卷，不能重复提交");

        Paper paper = paperMapper.selectById(record.getPaperId());
        int totalScore = 0;
        int objectiveScore = 0;

        if (dto.getAnswers() != null) {
            for (ExamSubmitDTO.AnswerItem item : dto.getAnswers()) {
                Question question = questionMapper.selectById(item.getQuestionId());
                if (question == null) continue;

                // 获取该题分值
                PaperQuestion pq = paperQuestionMapper.selectOne(
                        new LambdaQueryWrapper<PaperQuestion>()
                                .eq(PaperQuestion::getPaperId, paper.getId())
                                .eq(PaperQuestion::getQuestionId, item.getQuestionId()));
                int qScore = pq != null ? pq.getScore() : question.getScore();

                AnswerDetail detail = new AnswerDetail();
                detail.setExamRecordId(record.getId());
                detail.setQuestionId(item.getQuestionId());
                detail.setStudentAnswer(item.getAnswer());

                if ("ESSAY".equals(question.getType())) {
                    // 简答题需要教师批阅
                    detail.setIsCorrect(0);
                    detail.setScore(0);
                    detail.setMarked(0);
                } else {
                    // 客观题自动判分
                    boolean correct = isAnswerCorrect(question.getType(), item.getAnswer(), question.getAnswer());
                    detail.setIsCorrect(correct ? 1 : 0);
                    detail.setScore(correct ? qScore : 0);
                    detail.setMarked(1);
                    if (correct) {
                        totalScore += qScore;
                        objectiveScore += qScore;
                    }
                }
                answerDetailMapper.insert(detail);
            }
        }

        record.setStatus("FINISHED");
        record.setEndTime(LocalDateTime.now());
        record.setTotalScore(totalScore);
        record.setObjectiveScore(objectiveScore);
        record.setIsPassed(totalScore >= paper.getPassScore() ? 1 : 0);
        examRecordMapper.updateById(record);

        return totalScore;
    }

    /**
     * 教师批阅简答题
     */
    @Transactional
    public void gradeEssay(Long recordId, Long questionId, Integer score) {
        AnswerDetail detail = answerDetailMapper.selectOne(
                new LambdaQueryWrapper<AnswerDetail>()
                        .eq(AnswerDetail::getExamRecordId, recordId)
                        .eq(AnswerDetail::getQuestionId, questionId));
        if (detail == null) throw new IllegalArgumentException("答题记录不存在");
        detail.setScore(score);
        detail.setMarked(1);
        detail.setIsCorrect(score > 0 ? 1 : 0);
        answerDetailMapper.updateById(detail);

        // 更新总分
        recalcTotalScore(recordId);
    }

    private void recalcTotalScore(Long recordId) {
        List<AnswerDetail> details = answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>().eq(AnswerDetail::getExamRecordId, recordId));
        int total = details.stream().mapToInt(AnswerDetail::getScore).sum();
        ExamRecord record = examRecordMapper.selectById(recordId);
        Paper paper = paperMapper.selectById(record.getPaperId());
        record.setTotalScore(total);
        record.setIsPassed(total >= paper.getPassScore() ? 1 : 0);
        examRecordMapper.updateById(record);
    }

    private boolean isAnswerCorrect(String type, String studentAnswer, String correctAnswer) {
        if (studentAnswer == null) return false;
        if ("SINGLE".equals(type) || "JUDGE".equals(type)) {
            return studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
        }
        if ("MULTI".equals(type)) {
            // 多选题：排序后比较
            String sorted = studentAnswer.chars().sorted()
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString().toUpperCase();
            String sortedCorrect = correctAnswer.chars().sorted()
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString().toUpperCase();
            return sorted.equals(sortedCorrect);
        }
        return false;
    }

    // ========== 查询 ==========

    public PageResult<ExamRecordVO> getMyRecords(Integer pageNum, Integer pageSize) {
        Long studentId = UserContextUtil.getCurrentUserId();
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStudentId, studentId)
                .orderByDesc(ExamRecord::getCreateTime);
        Page<ExamRecord> result = examRecordMapper.selectPage(page, wrapper);
        List<ExamRecordVO> vos = new ArrayList<>();
        for (ExamRecord r : result.getRecords()) {
            vos.add(toVO(r));
        }
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    public ExamRecordVO getRecordDetail(Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        return toVO(record);
    }

    private ExamRecordVO toVO(ExamRecord r) {
        ExamRecordVO vo = new ExamRecordVO();
        vo.setId(r.getId());
        vo.setArrangementId(r.getArrangementId());
        vo.setPaperId(r.getPaperId());
        vo.setStatus(r.getStatus());
        vo.setStartTime(r.getStartTime());
        vo.setEndTime(r.getEndTime());
        vo.setTotalScore(r.getTotalScore());
        vo.setObjectiveScore(r.getObjectiveScore());
        vo.setIsPassed(r.getIsPassed());
        Paper paper = paperMapper.selectById(r.getPaperId());
        if (paper != null) {
            vo.setPaperName(paper.getPaperName());
        }
        if (r.getArrangementId() != null) {
            ExamArrangement arr = arrangementMapper.selectById(r.getArrangementId());
            if (arr != null) vo.setExamName(arr.getExamName());
        }
        return vo;
    }

    public List<AnswerDetail> getAnswerDetails(Long recordId) {
        return answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>().eq(AnswerDetail::getExamRecordId, recordId));
    }

    public long countExams() {
        return examRecordMapper.selectCount(null);
    }
}
