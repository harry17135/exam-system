package com.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.PageResult;
import com.exam.common.Result;
import com.exam.entity.*;
import com.exam.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public class TeacherController {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final PaperMapper paperMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;
    private final ExamArrangementMapper arrangementMapper;

    /**
     * 教师查看所有学生的考试记录（可按班级筛选）
     */
    @GetMapping("/exam-records")
    public Result<?> listExamRecords(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "20") Integer pageSize,
                                     @RequestParam(required = false) Long classId) {
        // 获取所有学生ID（按班级筛选）
        List<Long> studentIds;
        if (classId != null) {
            studentIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getClassId, classId).eq(User::getRole, "STUDENT")
            ).stream().map(User::getId).collect(Collectors.toList());
            if (studentIds.isEmpty()) {
                return Result.ok(PageResult.of(0, pageNum, pageSize, Collections.emptyList()));
            }
        } else {
            studentIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getRole, "STUDENT")
            ).stream().map(User::getId).collect(Collectors.toList());
        }

        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<ExamRecord>()
                .in(ExamRecord::getStudentId, studentIds)
                .orderByDesc(ExamRecord::getCreateTime);
        Page<ExamRecord> result = examRecordMapper.selectPage(page, wrapper);

        // 构建VO
        List<Map<String, Object>> vos = new ArrayList<>();
        Map<Long, String> userMap = userMapper.selectList(null).stream()
                .collect(Collectors.toMap(User::getId, User::getRealName));
        Map<Long, String> paperMap = paperMapper.selectList(null).stream()
                .collect(Collectors.toMap(Paper::getId, Paper::getPaperName));
        Map<Long, String> classMap = userMapper.selectList(null).stream()
                .filter(u -> u.getClassId() != null)
                .collect(Collectors.toMap(User::getId, u -> {
                    ClassEntity c = classMapper.selectById(u.getClassId());
                    return c != null ? c.getClassName() : "";
                }, (a, b) -> a));

        for (ExamRecord r : result.getRecords()) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", r.getId());
            vo.put("studentId", r.getStudentId());
            vo.put("studentName", userMap.getOrDefault(r.getStudentId(), ""));
            vo.put("className", classMap.getOrDefault(r.getStudentId(), ""));
            vo.put("paperName", paperMap.getOrDefault(r.getPaperId(), ""));
            vo.put("totalScore", r.getTotalScore());
            vo.put("objectiveScore", r.getObjectiveScore());
            vo.put("isPassed", r.getIsPassed());
            vo.put("status", r.getStatus());
            vo.put("startTime", r.getStartTime());
            vo.put("endTime", r.getEndTime());
            vos.add(vo);
        }

        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos));
    }

    /**
     * 获取某条考试记录的详细答题信息
     */
    @GetMapping("/exam-record/{recordId}/details")
    public Result<?> getRecordDetails(@PathVariable Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) return Result.error("记录不存在");

        List<AnswerDetail> details = answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>().eq(AnswerDetail::getExamRecordId, recordId));

        User student = userMapper.selectById(record.getStudentId());
        Paper paper = paperMapper.selectById(record.getPaperId());

        List<Map<String, Object>> vos = new ArrayList<>();
        for (AnswerDetail d : details) {
            Question q = questionMapper.selectById(d.getQuestionId());
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", d.getId());
            vo.put("questionId", d.getQuestionId());
            vo.put("questionTitle", q != null ? q.getTitle() : "");
            vo.put("questionType", q != null ? q.getType() : "");
            vo.put("correctAnswer", q != null ? q.getAnswer() : "");
            vo.put("studentAnswer", d.getStudentAnswer());
            vo.put("isCorrect", d.getIsCorrect());
            vo.put("score", d.getScore());
            vo.put("maxScore", q != null ? q.getScore() : 0);
            vo.put("marked", d.getMarked());
            vos.add(vo);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.getId());
        result.put("studentName", student != null ? student.getRealName() : "");
        result.put("paperName", paper != null ? paper.getPaperName() : "");
        result.put("totalScore", record.getTotalScore());
        result.put("status", record.getStatus());
        result.put("answers", vos);
        return Result.ok(result);
    }

    /**
     * 班级成绩统计
     */
    @GetMapping("/class-statistics")
    public Result<?> getClassStatistics() {
        List<ClassEntity> classes = classMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();

        for (ClassEntity c : classes) {
            List<Long> studentIds = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getClassId, c.getId()).eq(User::getRole, "STUDENT")
            ).stream().map(User::getId).collect(Collectors.toList());

            if (studentIds.isEmpty()) continue;

            List<ExamRecord> records = examRecordMapper.selectList(
                    new LambdaQueryWrapper<ExamRecord>()
                            .in(ExamRecord::getStudentId, studentIds)
                            .eq(ExamRecord::getStatus, "FINISHED"));

            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("classId", c.getId());
            stat.put("className", c.getClassName());
            stat.put("grade", c.getGrade());
            stat.put("studentCount", studentIds.size());
            stat.put("examCount", records.size());
            if (!records.isEmpty()) {
                double avg = records.stream().mapToInt(ExamRecord::getTotalScore).average().orElse(0);
                stat.put("averageScore", Math.round(avg * 100.0) / 100.0);
                long passed = records.stream().filter(r -> r.getIsPassed() == 1).count();
                stat.put("passRate", Math.round((double) passed / records.size() * 10000.0) / 100.0);
            } else {
                stat.put("averageScore", 0);
                stat.put("passRate", 0);
            }
            result.add(stat);
        }
        return Result.ok(result);
    }

    /**
     * 获取所有班级列表（教师用）
     */
    @GetMapping("/classes")
    public Result<?> listClasses() {
        return Result.ok(classMapper.selectList(null));
    }

    /**
     * 统计概览（教师视角）
     */
    @GetMapping("/overview")
    public Result<?> overview() {
        long studentCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "STUDENT"));
        long questionCount = questionMapper.selectCount(null);
        long paperCount = paperMapper.selectCount(null);
        long recordCount = examRecordMapper.selectCount(null);

        List<ExamRecord> finishedRecords = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "FINISHED"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("studentCount", studentCount);
        data.put("questionCount", questionCount);
        data.put("paperCount", paperCount);
        data.put("examCount", recordCount);
        if (!finishedRecords.isEmpty()) {
            data.put("averageScore", Math.round(finishedRecords.stream()
                    .mapToInt(ExamRecord::getTotalScore).average().orElse(0) * 100.0) / 100.0);
            long passed = finishedRecords.stream().filter(r -> r.getIsPassed() == 1).count();
            data.put("passRate", Math.round((double) passed / finishedRecords.size() * 10000.0) / 100.0);
        } else {
            data.put("averageScore", 0);
            data.put("passRate", 0);
        }
        return Result.ok(data);
    }
}
