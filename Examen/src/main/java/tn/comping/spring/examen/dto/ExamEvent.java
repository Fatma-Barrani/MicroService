package tn.comping.spring.examen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamEvent {
    private Long examId;
    private Long teacherId;
}
