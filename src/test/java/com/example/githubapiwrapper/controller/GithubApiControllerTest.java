package com.example.githubapiwrapper.controller;

import com.example.githubapiwrapper.model.Repository;
import com.example.githubapiwrapper.service.GithubApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(controllers = GithubApiController.class)
public class GithubApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GithubApiService githubApiService;

    @BeforeEach
    void setUp() {
        Repository repository = new Repository();
        repository.setName("repo1");
        Repository.Owner owner = new Repository.Owner();
        owner.setLogin("testuser");
        repository.setOwner(owner);

        Repository.Branch branch = new Repository.Branch();
        branch.setName("main");
        Repository.Branch.Commit commit = new Repository.Branch.Commit();
        commit.setSha("abcd1234");
        branch.setCommit(commit);

        repository.setBranches(List.of(branch));

        Mockito.when(githubApiService.getUserRepositories("testuser"))
                .thenReturn(Flux.just(repository));

        Mockito.when(githubApiService.getBranches("testuser", "repo1"))
                .thenReturn(Flux.just(branch));
    }

    @Test
    void listUserRepositories_ShouldReturnRepositories_WhenUserExists() {
        webTestClient.get()
                .uri("/api/v1/users/testuser/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("repo1")
                .jsonPath("$[0].owner.login").isEqualTo("testuser")
                .jsonPath("$[0].branches[0].name").isEqualTo("main")
                .jsonPath("$[0].branches[0].commit.sha").isEqualTo("abcd1234");
    }

    @Test
    void listUserRepositories_ShouldReturnNotFound_WhenUserDoesNotExist() {
        Mockito.when(githubApiService.getUserRepositories("nonexistentuser"))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        webTestClient.get()
                .uri("/api/v1/users/nonexistentuser/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("404 Not Found");
    }
}