package kr.codefor.blog.domain;

import lombok.Getter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
        if (targetClass.equals(Object.class)) {
            StringBuilder sb = new StringBuilder();
            sb.append(key).append(":").append("\n");
            for (String v : values) {
                sb.append("  ").append(v);
            }
            return sb.toString();
        } else if (
                targetClass.equals(LocalDateTime.class) ||
                        targetClass.equals(URL.class)
        ) {
            return key + ": \"" + values[0] + "\"\n";
        } else if (targetClass.equals(Arrays.class)) {
            String[] split = values[0].replaceAll("[\\[\\]]", "").split(",");
            return key + ": " + Arrays.toString(split) + "\n";
        }
        return key + ": \"" +
                values[0].replaceAll("['\"]", "") + "\"\n";
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
                        mappingHeader(URL.class, "avatar_url", this.profile.getAvatar_url()),
                        mappingHeader(String.class, "bio", this.profile.getBio())
                ) +
                mappingHeader(LocalDateTime.class, "date",
                        LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ) +
                mappingHeader(Arrays.class, "categories", this.categories.toString()) +
                mappingHeader(Arrays.class, "tags", this.tags.toString()) +
                "---\n" +
                content.replaceAll(
                        "\\(https://raw.githubusercontent.com/Code-for-Korea/c4k-blog-front/main/assets/img/posts/",
                        "(/assets/img/posts/"
                );

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(msg.getBytes());
        this.content = new String(encodedBytes);
    }
}
