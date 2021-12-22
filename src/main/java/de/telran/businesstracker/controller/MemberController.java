package de.telran.businesstracker.controller;

import de.telran.businesstracker.controller.dto.MemberDto;
import de.telran.businesstracker.mapper.MemberMapper;
import de.telran.businesstracker.model.Member;
import de.telran.businesstracker.service.MemberService;
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
@RequestMapping("/api/members")
public class MemberController {

    public final MemberService memberService;
    public final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @Hidden
    @PostMapping("")
    public ResponseEntity<MemberDto> createMember(@RequestBody @Valid MemberDto memberDto) throws URISyntaxException {
        Member member = memberService.add(memberDto.position, memberDto.projectId, memberDto.userId);
        memberDto.id = member.getId();
        return ResponseEntity
                .created(new URI("/api/members/" + member.getId()))
                .body(memberDto);
    }

    @Hidden
    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMember(@RequestBody @Valid MemberDto memberDto) throws HttpClientErrorException.BadRequest {
        memberService.edit(memberDto.id, memberDto.position);
    }

    @Operation(summary = "get list of members by project id")
    @GetMapping("/project/{id}")
    public List<MemberDto> getAllMembersByProjectId(@PathVariable long id) {
        return memberService.getAllByProjectId(id)
                .stream()
                .map(memberMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "get member by id")
    @GetMapping("/{id}")
    public MemberDto getMemberById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return memberMapper.toDto(member);
    }

    @Hidden
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        memberService.removeById(id);
    }
}
