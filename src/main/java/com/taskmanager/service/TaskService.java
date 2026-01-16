package com.taskmanager.service;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.request.TaskStatusRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.exception.BusinessException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final UserService userService;

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa", id));
    }

    public TaskResponse findByIdResponse(Long id, User currentUser) {
        Task task = findById(id);
        validateTaskAccess(task, currentUser);
        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> findAllByProject(Long projectId, User currentUser) {
        Project project = projectService.findById(projectId);
        projectService.validateOwnership(project, currentUser);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request, User currentUser) {
        Project project = projectService.findById(projectId);
        projectService.validateOwnership(project, currentUser);

        validateDeadline(request.getDeadline());

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userService.findById(request.getAssignedUserId());
        }

        Task task = taskMapper.toEntity(request, project, assignedUser);
        task.setStatus(TaskStatus.TODO);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request, User currentUser) {
        Task task = findById(id);
        validateTaskAccess(task, currentUser);

        validateDeadline(request.getDeadline());

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userService.findById(request.getAssignedUserId());
        }

        taskMapper.updateEntity(task, request, assignedUser);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusRequest request, User currentUser) {
        Task task = findById(id);
        validateTaskAccess(task, currentUser);

        task.setStatus(request.getStatus());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Task task = findById(id);
        validateTaskAccess(task, currentUser);

        taskRepository.delete(task);
    }

    private void validateTaskAccess(Task task, User user) {
        if (!task.getProject().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Você não tem permissão para acessar esta tarefa");
        }
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new BusinessException("Prazo não pode ser no passado");
        }
    }
}
