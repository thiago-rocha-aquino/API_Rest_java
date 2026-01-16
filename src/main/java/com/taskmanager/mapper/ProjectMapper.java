package com.taskmanager.mapper;

import com.taskmanager.dto.request.ProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .ownerName(project.getOwner().getName())
                .createdAt(project.getCreatedAt())
                .build();
    }

    public Project toEntity(ProjectRequest request, User owner) {
        if (request == null) {
            return null;
        }

        return Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();
    }

    public void updateEntity(Project project, ProjectRequest request) {
        if (request == null || project == null) {
            return;
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
    }
}
