package de.telran.businesstracker.controller;

import de.telran.businesstracker.controller.dto.ResourceDto;
import de.telran.businesstracker.controller.dto.TaskDto;
import de.telran.businesstracker.controller.dto.TaskToAddDto;
import de.telran.businesstracker.mapper.TaskMapper;
import de.telran.businesstracker.model.Task;
import de.telran.businesstracker.service.ResourceService;
import de.telran.businesstracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {"http://localhost:3000"})
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final ResourceService resourceService;

    public TaskController(TaskService taskService, TaskMapper taskMapper, ResourceService resourceService) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.resourceService = resourceService;
    }

    @Operation(summary = "add new task")
    @PostMapping("")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskToAddDto taskDto) throws URISyntaxException {
        Task task = taskService.add(taskDto.name, taskDto.finished, taskDto.active, taskDto.mileStoneId, taskDto.memberId);
        TaskDto dto = taskMapper.toDto(task);

        for (ResourceDto resource : taskDto.resources)
            resourceService.add(resource.name, resource.hours, resource.cost, task.getId());

        return ResponseEntity
                .created(new URI("/api/tasks/" + task.getId()))
                .body(dto);
    }

    @Operation(summary = "update task")
    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTask(@RequestBody @Valid TaskDto taskDto) throws HttpClientErrorException.BadRequest {
        taskService.edit(taskDto.id, taskDto.name, taskDto.finished, taskDto.active);
    }

    @Operation(summary = "get list of active tasks by project id")
    @GetMapping("project/{id}/active")
    public List<TaskDto> getAllActiveTasksByProjectId(@PathVariable long id) {
        return taskService.getAllActiveTasksByProjectId(id)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "get list of tasks by milestone id")
    @GetMapping("milestone/{id}")
    public List<TaskDto> getAllTasksByMileStoneId(@PathVariable long id) {
        return taskService.getAllByMileStoneId(id)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "get task by id")
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return taskMapper.toDto(task);
    }

    @Operation(summary = "delete task by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Long id) {
        taskService.removeById(id);
    }
}
