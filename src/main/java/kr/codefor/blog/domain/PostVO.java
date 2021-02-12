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

    private String mappingHeader(Class<?> targetClass, String key, String... values) {
        if (targetClass.isInstance(Object.class)) {
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\n");
            for (String v : values) {
                sb.append("  ").append(v);
            }
            return sb.toString();
        } else if (targetClass.getClass().isInstance(String.class)) {
            return key + ": \"" + values[0].replaceAll("\"", "\\\"") + "\"\n";
        }
        return "";
    }

    public PostVO(String title, ProfileVO profile, List<String> categories, List<String> tags, String content) {
        this.title = title.replaceAll("'", "\\'");
        this.profile = profile;
        this.categories = categories;
        this.tags = tags;

        String msg = "" +
                "---\n" +
                mappingHeader(String.class, "title", this.title) +
                mappingHeader(Object.class, "author",
                        mappingHeader(String.class, "id", this.profile.getId()),
                        mappingHeader(String.class, "login", this.profile.getLogin()),
                        mappingHeader(String.class, "name", this.profile.getName()),
                        mappingHeader(String.class, "avatar_url", this.profile.getAvatar_url()),
                        mappingHeader(String.class, "bio", this.profile.getBio())
                ) +
                mappingHeader(String.class, "date",
                        LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ) +
                mappingHeader(String.class, "categories", this.categories.toString()) +
                mappingHeader(String.class, "tags", this.tags.toString()) +
                "---\n" +
                content;

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(msg.getBytes());
        this.content = new String(encodedBytes);
    }
}
