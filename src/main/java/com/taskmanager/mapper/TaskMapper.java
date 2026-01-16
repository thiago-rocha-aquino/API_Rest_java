package com.taskmanager.mapper;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponse.TaskResponseBuilder builder = TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .createdAt(task.getCreatedAt());

        if (task.getAssignedUser() != null) {
            builder.assignedUserId(task.getAssignedUser().getId())
                   .assignedUserName(task.getAssignedUser().getName());
        }

        return builder.build();
    }

    public Task toEntity(TaskRequest request, Project project, User assignedUser) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .project(project)
                .assignedUser(assignedUser)
                .build();
    }

    public void updateEntity(Task task, TaskRequest request, User assignedUser) {
        if (request == null || task == null) {
            return;
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setAssignedUser(assignedUser);
    }
}
