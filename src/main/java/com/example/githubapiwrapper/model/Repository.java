package com.example.githubapiwrapper.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Repository {

    private String name;
    private Owner owner;
    private boolean fork;
    private List<Branch> branches;

    @Getter
    @Setter
    public static class Owner {

        private String login;
    }

    @Getter
    @Setter
    public static class Branch {

        private String name;
        private Commit commit;

        @Getter
        @Setter
        public static class Commit {

            private String sha;
        }
    }
}
