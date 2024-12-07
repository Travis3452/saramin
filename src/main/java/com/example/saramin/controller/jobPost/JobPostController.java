package com.example.saramin.controller.jobPost;

import com.example.saramin.entity.dto.JobPost.JobPostUpdateForm;
import com.example.saramin.entity.dto.JobPost.JobsRequestForm;
import com.example.saramin.service.JobPostService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "채용 공고 관련 관련 API")
public class JobPostController {
    private final JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getJobPosts(
            @Parameter(description = "현재 페이지 번호", example = "1") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "정렬 기준: newest(최신순) 혹은 alphabetical(가나다순)", example = "newest") @RequestParam(required = false) String sortBy,
            @Parameter(description = "지역 필터(null일 경우 필터링하지 않음)", example = "서울전체") @RequestParam(required = false) String workplace,
            @Parameter(description = "경력 필터(null일 경우 필터링하지 않음) 예시의 경우 경력 5년 이하를 의미", example = "5") @RequestParam(required = false) Integer career,
            @Parameter(description = "기술 스택 필터(null일 경우 필터링하지 않음) 그누보드, 라즈베리파이 등", example = "라즈베리파이") @RequestParam(required = false) String skillStack,
            @Parameter(description = "검색 방식(null일 경우 검색하지 않음) title(제목) 혹은 companyName(회사명)", example = "title") @RequestParam(required = false) String searchBy,
            @Parameter(description = "검색에 사용할 키워드", example = "개발") @RequestParam(required = false) String keyword) {

        JobsRequestForm requestForm = new JobsRequestForm(currentPage, sortBy, workplace, career, skillStack, searchBy, keyword);

        Map<String, Object> response = jobPostService.getJobPosts(requestForm);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJobPostDetail(
            @Parameter(description = "채용 공고 ID", example = "1") @PathVariable Long id) {
        Map<String, Object> response = jobPostService.getJobPostDetail(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateJobPost(
            @Parameter(description = "채용 공고 ID", example = "1") @PathVariable Long id,
            @RequestBody JobPostUpdateForm updateForm) {
        Map<String, Object> response = jobPostService.updateJobPost(id, updateForm);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteJobPost(
            @Parameter(description = "채용 공고 ID", example = "1") @PathVariable Long id) {

        Map<String, Object> response = jobPostService.deleteJobPost(id);
        return ResponseEntity.ok(response);
    }
}