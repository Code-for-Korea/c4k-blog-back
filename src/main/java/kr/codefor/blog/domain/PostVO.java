package kr.codefor.blog.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Getter
public class PostVO {
    private final String title;
    private final String content;
    private final String author;
    private final List<String> categories;
    private final List<String> tags;

    public PostVO(String title, String author, List<String> categories, List<String> tags, String content) {
        this.title = title.replaceAll("'", "\\'");
        this.author = author.replaceAll("'", "\\'");
        this.categories = categories;
        this.tags = tags;

        String msg = "" +
                "---\n" +
                "title: '" + this.title + "'\n" +
                "author: '" + this.author + "'\n" +
                "date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) + " +0900\n" +
                "categories: " + this.categories + "\n" +
                "tags: " + this.tags + "\n" +
                "---\n" +
                content;

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(msg.getBytes());
        this.content = new String(encodedBytes);
    }
}
