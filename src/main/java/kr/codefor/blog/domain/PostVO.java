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

    private final ProfileVO profile;

    private final List<String> categories;
    private final List<String> tags;

    private String mappingHeader(String key, String value) {
        return key + ": '" + value + "'\n";
    }

    private String mappingHeader(String key, String value, String delimiter) {
        return key + ": " + delimiter + value + delimiter + "\n";
    }

    public PostVO(String title, ProfileVO profile, List<String> categories, List<String> tags, String content) {
        this.title = title.replaceAll("'", "\\'");
        this.profile = profile;
        this.categories = categories;
        this.tags = tags;

        String msg = "" +
                "---\n" +
                mappingHeader("title", this.title) +
                mappingHeader("author", this.profile.toString(), "") +
                mappingHeader("date",
                        LocalDateTime.now().minusHours(9).format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        ) + " +0900") +
                mappingHeader("categories", this.categories.toString()) +
                mappingHeader("tags", this.tags.toString()) +
                "---\n" +
                content;

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(msg.getBytes());
        this.content = new String(encodedBytes);
    }
}
