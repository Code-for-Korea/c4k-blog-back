package kr.codefor.blog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileVO {
    private final String id;
    private final String login;
    private final String name;
    private final String avatar_url;
    private final String bio;
}
