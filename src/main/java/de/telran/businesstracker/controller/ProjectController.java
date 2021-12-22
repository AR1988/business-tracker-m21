package de.telran.businesstracker.controller;

import de.telran.businesstracker.controller.dto.ProjectDto;
import de.telran.businesstracker.mapper.ProjectMapper;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.service.ProjectService;
import io.swagger.v3.oas.annotations.Hidden;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @Hidden
    @PostMapping("")
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) throws URISyntaxException {
        Project project = projectService.add(projectDto.name, projectDto.userId);
        projectDto.id = project.getId();
        return ResponseEntity
                .created(new URI("/api/projects/" + project.getId()))
                .body(projectDto);
    }

    @Hidden
    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProject(@RequestBody @Valid ProjectDto projectDto) throws HttpClientErrorException.BadRequest {
        projectService.edit(projectDto.id, projectDto.name);
    }

    @Hidden
    @GetMapping("")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAll()
                .stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "get project by id")
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable Long id) {
        Project project = projectService.getById(id);
        return projectMapper.toDto(project);
    }

    @Hidden
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.removeById(id);
    }
}
