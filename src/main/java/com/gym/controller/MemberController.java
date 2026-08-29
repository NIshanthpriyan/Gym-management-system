package com.gym.controller;

import com.gym.dto.MemberDTO;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // Admin endpoint to get all members (paginated, searchable, filterable)
    @GetMapping("/admin/members")
    public ResponseEntity<Page<MemberDTO>> getAllMembers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "joinDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        Page<MemberDTO> members = memberService.getAllMembers(search, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/member/user/{userId}")
    public ResponseEntity<MemberDTO> getMemberByUserId(@PathVariable Long userId) {
        MemberDTO member = memberService.getMemberByUserId(userId);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/member/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/admin/member/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("Member deleted successfully.");
    }

    @GetMapping("/admin/members/expiring")
    public ResponseEntity<List<MemberDTO>> getExpiringMemberships(@RequestParam(value = "days", defaultValue = "7") int days) {
        List<MemberDTO> list = memberService.getExpiringMemberships(days);
        return ResponseEntity.ok(list);
    }
}
