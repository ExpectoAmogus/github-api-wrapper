package com.example.githubapiwrapper.service;


import com.example.githubapiwrapper.model.Repository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

@SpringBootTest
public class GithubApiServiceTest {

    private static MockWebServer mockWebServer;

    private GithubApiService githubApiService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        githubApiService = new GithubApiService(mockWebServer.url("/").toString());
    }

    @Test
    void getUserRepositories_ShouldReturnRepositories_WhenUserExists() {
        String repositoriesJson = """
                [
                    {
                        "name": "repo1",
                        "owner": {"login": "testuser"},
                        "fork": false
                    },
                    {
                        "name": "repo2",
                        "owner": {"login": "testuser"},
                        "fork": true
                    }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(repositoriesJson)
                .addHeader("Content-Type", "application/json"));

        Flux<Repository> repositories = githubApiService.getUserRepositories("testuser");

        StepVerifier.create(repositories)
                .expectNextMatches(repo -> repo.getName().equals("repo1"))
                .verifyComplete();
    }

    @Test
    void getBranches_ShouldReturnBranches_WhenRepositoryExists() {
        String branchesJson = """
                [
                    {
                        "name": "main",
                        "commit": {"sha": "abcd1234"}
                    },
                    {
                        "name": "dev",
                        "commit": {"sha": "efgh5678"}
                    }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(branchesJson)
                .addHeader("Content-Type", "application/json"));

        Flux<Repository.Branch> branches = githubApiService.getBranches("testuser", "repo1");

        StepVerifier.create(branches)
                .expectNextMatches(branch -> branch.getName().equals("main") && branch.getCommit().getSha().equals("abcd1234"))
                .expectNextMatches(branch -> branch.getName().equals("dev") && branch.getCommit().getSha().equals("efgh5678"))
                .verifyComplete();
    }
}