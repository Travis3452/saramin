package com.example.saramin.service;

import com.example.saramin.entity.model.Company;
import com.example.saramin.entity.model.JobPost;
import com.example.saramin.repository.CompanyRepository;
import com.example.saramin.repository.JobPostRepository;
import com.example.saramin.util.CrawlerValidator;
import com.example.saramin.util.DateParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerService {
    private static final String BASE_URL = "https://www.saramin.co.kr/zf_user/jobs/list/job-category?cat_kewd=%d&panel_type=&search_optional_item=n&search_done=y&panel_count=y&preview=y";
    private static final int MAX_JOBS_PER_KEY = 5;
    private static final int TOTAL_MAX_JOBS = 10;

    private final CompanyRepository companyRepository;
    private final JobPostRepository jobPostRepository;
    private final CrawlerValidator crawlerValidator;

    @PostConstruct
    public void init() {
        log.info("Start crawl");
        try {
            crawlJobPosts();
        } catch (Exception e) {
            log.error("Error during crawling job posts: {}", e.getMessage(), e);
        }
        log.info("End crawl");
    }

    public void crawlJobPosts() {
        int totalJobCount = 0;

        for (int key = 185; key <= 318; key++) {
            if (totalJobCount >= TOTAL_MAX_JOBS) break;
            log.info("Processing category key: {}", key);

            String url = String.format(BASE_URL, key);
            addRandomDelay();

            try {
                Document doc = Jsoup.connect(url).get();

                String skillStack = doc.select(".wrap_title_recruit .value").text();
                log.info("Skill Stack: {}", skillStack);

                Elements jobItems = doc.select(".list_recruiting .list_body .list_item");
                int jobCountForKey = 0;

                for (Element jobItem : jobItems) {
                    if (jobCountForKey >= MAX_JOBS_PER_KEY || totalJobCount >= TOTAL_MAX_JOBS) break;

                    String companyName = jobItem.select(".company_nm .str_tit").text();
                    log.info("Company Name: {}", companyName);

                    String title = jobItem.select(".notification_info .job_tit .str_tit").text();
                    log.info("Title: {}", title);

                    String deadlineText = jobItem.select(".support_detail > .date").text();
                    Date deadlineDate = DateParser.parseDeadlineDate(deadlineText);
                    log.info("Deadline Date: {}", deadlineDate);

                    String postDateText = jobItem.select(".support_detail > .deadlines").text();
                    Date postDate = DateParser.parsePostDate(postDateText);
                    log.info("Post Date: {}", postDate);

                    String workPlace = jobItem.select(".recruit_info > ul > li p.work_place").text();
                    String career = jobItem.select(".recruit_info > ul > li p.career").text();
                    String education = jobItem.select(".recruit_info > ul > li p.education").text();

                    log.info("Work Place: {}", workPlace);
                    log.info("Career: {}", career);
                    log.info("Education: {}", education);

                    if (crawlerValidator.isAnyFieldNull(companyName, title, workPlace, career, education, deadlineDate, postDate)) {
                        log.warn("Skipping job post due to null values: CompanyName={}, Title={}, DeadlineDate={}, PostDate={}, WorkPlace={}, Career={}, Education={}",
                                companyName, title, deadlineDate, postDate, workPlace, career, education);
                        continue;
                    }

                    Optional<Company> existingCompany = companyRepository.findByCompanyName(companyName);
                    Company company;
                    if (existingCompany.isPresent()) {
                        company = existingCompany.get();
                        log.info("Existing company found: {}", company.getCompanyName());
                    } else {
                        company = Company.builder()
                                .companyName(companyName)
                                .build();
                        companyRepository.save(company);
                        log.info("Saved new company: {}", company.getCompanyName());
                    }

                    if (!crawlerValidator.isJobPostValid(title, company) || !crawlerValidator.isJobPostUnique(title, company)) {
                        log.info("Job post already exists or is invalid: {}", title);
                        continue;
                    }

                    JobPost jobPost = JobPost.builder()
                            .title(title)
                            .skillStack(skillStack)
                            .company(company)
                            .workPlace(workPlace)
                            .career(career)
                            .education(education)
                            .postDate(postDate)
                            .deadline(deadlineDate)
                            .build();

                    jobPostRepository.save(jobPost);
                    log.info("Saved job post: {}", jobPost.getTitle());

                    jobCountForKey++;
                    totalJobCount++;
                }
            } catch (IOException e) {
                log.error("Error fetching the page for key {}: {}", key, e.getMessage());
            }
        }
    }

    private void addRandomDelay() {
        try {
            int delay = ThreadLocalRandom.current().nextInt(1000, 3001);
            Thread.sleep(delay);
            log.info("Waiting for {} milliseconds", delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Delay was interrupted", e);
        }
    }
}