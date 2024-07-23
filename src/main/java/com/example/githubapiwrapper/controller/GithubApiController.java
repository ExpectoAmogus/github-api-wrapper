package com.example.githubapiwrapper.controller;

import com.example.githubapiwrapper.exception.ErrorResponse;
import com.example.githubapiwrapper.model.Repository;
import com.example.githubapiwrapper.service.GithubApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class GithubApiController {
    private final GithubApiService githubApiService;

    public GithubApiController(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @GetMapping("/users/{username}/repos")
    public Flux<Repository> listUserRepositories(@PathVariable String username) {
        return githubApiService.getUserRepositories(username)
                .flatMap(repo -> githubApiService.getBranches(username, repo.getName())
                        .collectList()
                        .map(branches -> {
                            repo.setBranches(branches);
                            return repo;
                        }));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatusCode().value(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
}
