package com.example.githubapiwrapper.service;

import com.example.githubapiwrapper.model.Repository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class GithubApiService {
    private final WebClient webClient;

    public GithubApiService(@Value("${github.api.url}") String githubApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(githubApiUrl)
                .build();
    }

    public Flux<Repository> getUserRepositories(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(Repository.class)
                .filter(repo -> !repo.isFork());
    }

    public Flux<Repository.Branch> getBranches(String username, String repoName) {
        return webClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .bodyToFlux(Repository.Branch.class);
    }
}
