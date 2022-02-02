package de.telran.businesstracker.mapper;

import de.telran.businesstracker.controller.dto.TaskDto;
import de.telran.businesstracker.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())

                .name(task.getName())
                .finished(task.isFinished())
                .active(task.isActive())

                .milestoneId(task.getMilestone().getId())
                .memberId(task.getResponsibleMember().getId())
                .build();
    }
}
