package kr.codefor.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.io.File;
import java.util.Scanner;

@SpringBootTest(properties = "" +
        "spring.config.location=" +
        "classpath:/application.yml," +
        "optional:C:/repository/_secrets/c4k-blog.yml," +
        "optional:/home/ec2-user/_secrets/c4k-blog.yml")
class EditorApplicationTests {
    private String generateRandom(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void GitHub이미지업로드() {

    }

    @Test
    void GitHubDirect이미지업로드테스트() {
        String mime = "image/jpeg";
        StringBuilder imageEncode = new StringBuilder();
        try {
            File file = new File("D:\\encode.txt");
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                imageEncode.append(scan.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        System.out.println(imageEncode.toString().substring(0, 100));

        String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = nowDate + "-" + generateRandom(5);
        MimeType mimeType = MimeTypeUtils.parseMimeType(mime);
        String fileExtension = mimeType.getSubtype();
        String url = "https://api.github.com/repos/Code-for-Korea/c4k-blog-front/contents/assets/img/posts/" +
                nowDate + "/" + fileName + "." + fileExtension;

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/vnd.github.v3+json");
        headers.set("Authorization", "token " + "");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "[NEW] " + fileName + "." + fileExtension);
        responseBody.put("content", imageEncode.toString());

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(responseBody, headers);

        try {
            HttpEntity<Map> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.PUT, requestEntity, Map.class, responseBody);
            System.out.println(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
    }

}
