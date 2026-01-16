package com.taskmanager.service;

import com.taskmanager.dto.request.ProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.mapper.ProjectMapper;
import com.taskmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto", id));
    }

    public ProjectResponse findByIdResponse(Long id, User currentUser) {
        Project project = findById(id);
        validateOwnership(project, currentUser);
        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> findAllByOwner(User owner) {
        return projectRepository.findByOwnerId(owner.getId())
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request, User owner) {
        Project project = projectMapper.toEntity(request, owner);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request, User currentUser) {
        Project project = findById(id);
        validateOwnership(project, currentUser);

        projectMapper.updateEntity(project, request);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Project project = findById(id);
        validateOwnership(project, currentUser);

        projectRepository.delete(project);
    }

    public void validateOwnership(Project project, User user) {
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Você não tem permissão para acessar este projeto");
        }
    }

    public boolean isOwner(Long projectId, Long userId) {
        return projectRepository.existsByIdAndOwnerId(projectId, userId);
    }
}
