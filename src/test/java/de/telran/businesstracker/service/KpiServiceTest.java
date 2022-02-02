package de.telran.businesstracker.service;

import de.telran.businesstracker.model.Milestone;
import de.telran.businesstracker.model.Project;
import de.telran.businesstracker.model.Roadmap;
import de.telran.businesstracker.model.User;
import de.telran.businesstracker.repositories.MilestoneRepository;
import de.telran.businesstracker.repositories.ProjectRepository;
import de.telran.businesstracker.repositories.RoadmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KpiServiceTest {

    @Mock
    MilestoneRepository milestoneRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    RoadmapRepository roadmapRepository;

    @InjectMocks
    KpiService kpiService;

    private Project project;
    private Roadmap roadmap;
    private Milestone milestone;

    @BeforeEach
    public void beforeEachTest() {
        User user = new User(5L, "Ivan", "Petrov", "Boss", "img-url", new LinkedHashSet<>(), new ArrayList<>());
        project = new Project(4L, "Great project", user, new LinkedHashSet<>(), new LinkedHashSet<>());
        roadmap = new Roadmap(3L, "Roadmap", LocalDate.now(), project, new LinkedHashSet<>());
        milestone = new Milestone(1L, "Milestone", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>(), new LinkedHashSet<>());
    }

    @Test
    public void testAdd_success() {
        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        kpiService.add(milestone.getId(), "some kpi name");

        verify(milestoneRepository, times(1)).save(any());
        verify(milestoneRepository, times(1)).save(argThat(savedMilestone -> savedMilestone.getKpis().size() == 1));
    }

    @Test
    public void testAdd_roadmapDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                kpiService.add(milestone.getId(), "some kpi name"));

        verify(milestoneRepository, times(1)).findById(any());
        assertEquals("Error! This milestone doesn't exist in our DB", exception.getMessage());
    }

    @Test
    public void testGetAllKpisByProject_twoRoadMapsWithEachOneMileStones_sixElementFound() {
        Milestone milestone1 = milestone;
        milestone1.addKpi("kpi_01_01");
        milestone1.addKpi("kpi_01_02");
        milestone1.addKpi("kpi_01_03");
        milestone1.addKpi("kpi_01_04");

        Roadmap roadmap2 = new Roadmap("Roadmap_02", LocalDate.now(), project);
        Milestone milestone2 = new Milestone("Milestone_02", LocalDate.now(), LocalDate.now().plusDays(10), roadmap2, new ArrayList<>());
        milestone2.addKpi("kpi_02_01");
        milestone2.addKpi("kpi_02_02");

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(milestoneRepository.findAllByRoadmapProject(project)).thenReturn(Arrays.asList(milestone1, milestone2));

        List<String> kpis = kpiService.getAllKpiByProject(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(milestoneRepository, times(1)).findAllByRoadmapProject(project);

        assertTrue(kpis.contains("kpi_01_01"));
        assertTrue(kpis.contains("kpi_01_02"));
        assertTrue(kpis.contains("kpi_01_03"));
        assertTrue(kpis.contains("kpi_01_04"));
        assertTrue(kpis.contains("kpi_02_01"));
        assertTrue(kpis.contains("kpi_02_01"));
    }

    @Test
    public void testGetAllKpisByProject_oneRoadMapWithTreeMileStones_sevenElementFound() {
        Milestone milestone1 = milestone;
        milestone1.addKpi("kpi_01_01");
        milestone1.addKpi("kpi_01_02");
        milestone1.addKpi("kpi_01_03");
        milestone1.addKpi("kpi_01_04");

        Milestone milestone2 = new Milestone("Milestone_02", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>());
        milestone2.addKpi("kpi_02_01");
        milestone2.addKpi("kpi_02_02");

        Milestone milestone3 = new Milestone("Milestone_03", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>());
        milestone2.addKpi("kpi_03_01");

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(milestoneRepository.findAllByRoadmapProject(project)).thenReturn(Arrays.asList(milestone1, milestone2, milestone3));

        List<String> kpis = kpiService.getAllKpiByProject(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(milestoneRepository, times(1)).findAllByRoadmapProject(project);

        assertEquals(7, kpis.size());
        assertTrue(kpis.contains("kpi_01_01"));
        assertTrue(kpis.contains("kpi_01_02"));
        assertTrue(kpis.contains("kpi_01_03"));
        assertTrue(kpis.contains("kpi_01_04"));
        assertTrue(kpis.contains("kpi_02_01"));
        assertTrue(kpis.contains("kpi_02_01"));
        assertTrue(kpis.contains("kpi_03_01"));
    }

    @Test
    public void testGetAllKpisByProject_oneRoadMapWithTwoMileStones_noKpiExist_emptyList() {
        Milestone milestone1 = new Milestone("Milestone_01", roadmap, new ArrayList<>());
        Milestone milestone2 = new Milestone("Milestone_02", roadmap, new ArrayList<>());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(milestoneRepository.findAllByRoadmapProject(project)).thenReturn(Arrays.asList(milestone1, milestone2));

        List<String> kpis = kpiService.getAllKpiByProject(project.getId());

        verify(projectRepository, times(1)).findById(project.getId());
        verify(milestoneRepository, times(1)).findAllByRoadmapProject(project);
        assertEquals(0, kpis.size());
    }

    @Test
    public void testGetAllKpiByMileStone_fourElementFound() {
        milestone.addKpi("kpi_01_01");
        milestone.addKpi("kpi_01_02");
        milestone.addKpi("kpi_01_03");
        milestone.addKpi("kpi_01_04");

        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));
        List<String> kpis = kpiService.getAllKpiByMileStone(milestone.getId());

        verify(milestoneRepository, times(1)).findById(milestone.getId());
        assertEquals(4, kpis.size());
    }

    @Test
    public void testGetAllKpiByBMileStone_emptyList() {

        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));
        List<String> kpis = kpiService.getAllKpiByMileStone(milestone.getId());

        verify(milestoneRepository, times(1)).findById(milestone.getId());
        assertEquals(0, kpis.size());
    }


    @Test
    public void testGetAllKpiByRoadMap_oneProjectOneRoadMapsTreeMileStoneOnEachRoadMap_fourElementFound() {

        Milestone milestone1 = new Milestone("Milestone_01", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>());
        milestone1.addKpi("kpi_01_01");

        Milestone milestone2 = new Milestone("Milestone_02", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>());
        milestone2.addKpi("kpi_01_01");
        milestone2.addKpi("kpi_01_02");

        Milestone milestone3 = new Milestone("Milestone_03", LocalDate.now(), LocalDate.now().plusDays(10), roadmap, new ArrayList<>());
        milestone3.addKpi("kpi_01_01");

        when(roadmapRepository.findById(roadmap.getId())).thenReturn(Optional.of(roadmap));
        when(milestoneRepository.findAllByRoadmap(roadmap)).thenReturn(Arrays.asList(milestone1, milestone2, milestone3));

        List<String> kpis = kpiService.getAllKpiByRoadMap(roadmap.getId());

        verify(roadmapRepository, times(1)).findById(roadmap.getId());
        verify(milestoneRepository, times(1)).findAllByRoadmap(roadmap);

        assertEquals(4, kpis.size());
    }


    @Test
    public void testGetAllKpiByRoadMap_emptyList() {
        when(roadmapRepository.findById(roadmap.getId())).thenReturn(Optional.of(roadmap));
        List<String> kpis = kpiService.getAllKpiByRoadMap(roadmap.getId());

        verify(roadmapRepository, times(1)).findById(roadmap.getId());
        assertEquals(0, kpis.size());
    }

    @Test
    public void testRemoveKpi_kpiRemoved() {
        milestone.addKpi("kpi_01_01");
        milestone.addKpi("kpi_01_02");

        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        kpiService.removeKpi(milestone.getId(), 0);

        verify(milestoneRepository, times(1)).findById(milestone.getId());
        verify(milestoneRepository, times(1)).save(argThat(
                argument ->
                        argument.getId().equals(milestone.getId()) &&
                                argument.getKpis().size() == 1 &&
                                argument.getName().equals(milestone.getName()) &&
                                !argument.getKpis().contains("kpi_01_01")
        ));
    }

    @Test
    public void testRemoveKpi_kpiNotExist_EntityNotFoundException() {
        when(milestoneRepository.findById(milestone.getId())).thenReturn(Optional.of(milestone));

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                kpiService.removeKpi(milestone.getId(), 1));

        verify(milestoneRepository, times(1)).findById(milestone.getId());
        assertEquals("Error! This kpi doesn't exist in our DB", exception.getMessage());
    }

    @Test
    void testProjectById_projectDoesNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class, () -> kpiService.getProjectById(1));

        verify(projectRepository, times(1)).findById(1L);
        assertEquals("Error! This project doesn't exist in our DB", exception.getMessage());
    }

    @Test
    void testGetRoadMapById_roadMapNotExist_EntityNotFoundException() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> kpiService.getRoadMapById(1));

        verify(roadmapRepository, times(1)).findById(1L);
        assertEquals("Error! This roadmap doesn't exist in our DB", exception.getMessage());

    }
}
