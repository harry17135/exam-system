package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.ExamRecord;
import com.exam.entity.User;
import com.exam.mapper.ExamRecordMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.UserMapper;
import com.exam.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final ExamRecordMapper examRecordMapper;
    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;

    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();
        vo.setTotalStudents(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "STUDENT")));
        vo.setTotalTeachers(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "TEACHER")));
        vo.setTotalQuestions(questionMapper.selectCount(null));
        vo.setTotalExams(examRecordMapper.selectCount(null));

        List<ExamRecord> records = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "FINISHED"));
        if (!records.isEmpty()) {
            double avg = records.stream().mapToInt(ExamRecord::getTotalScore).average().orElse(0);
            vo.setAverageScore(Math.round(avg * 100.0) / 100.0);
            long passed = records.stream().filter(r -> r.getIsPassed() == 1).count();
            vo.setPassRate(Math.round((double) passed / records.size() * 10000.0) / 100.0);
        }
        return vo;
    }
}
