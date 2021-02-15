package kr.codefor.blog.controller;

import kr.codefor.blog.domain.*;
import kr.codefor.blog.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/github")
public class GithubController {

    private final SessionService sessionService;

    @Value("${api.github.clientid}")
    private String client_id;

    @Value("${api.github.clientSecret}")
    private String clientSecret;

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

    @PostMapping("/post")
    public JSONResponse CreateNewPost(
            @CookieValue(value = "GSESSIONID", required = false) String gsession_id,
            @RequestBody PostVO post) {
        HashMap<String, Object> result = new HashMap<>();

        Session one = sessionService.findOne(gsession_id);

        if (one == null) {
            result.put("error", true);
        } else {
            String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName =
                    nowDate +
                            post.getTitle()
                                    .replaceAll("[^ 가-힣a-zA-Z0-9]", "")
                                    .trim()
                                    .replaceAll("\s", "-") +
                            "-" + generateRandom(5) + ".md";
            String url = "https://api.github.com/repos/Code-for-Korea/c4k-blog-front/contents/_posts/" +
                    nowDate + "/" + fileName;

            System.out.println("fileName: " + fileName);
            System.out.println("Title: " + post.getTitle());

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("accept", "application/vnd.github.v3+json");
            headers.set("Authorization", "token " + one.getAccessToken());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "[NEW] " + fileName);
            responseBody.put("content", post.getContent());

            HttpEntity<Map> requestEntity = new HttpEntity<Map>(responseBody, headers);

            try {
                HttpEntity<Map> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.PUT, requestEntity, Map.class, responseBody);
                result.put("msg", responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                result.put("error", true);
                result.put("msg", e.getMessage());
                result.put("status", e.getStatusCode().value());
            }
        }
        return new JSONResponse(result);
    }

    @PostMapping("/image")
    public JSONResponse CreateNewImage(
            @CookieValue(value = "GSESSIONID", required = false) String gsession_id,
            @RequestBody ImageVO image) {
        HashMap<String, Object> result = new HashMap<>();

        Session one = sessionService.findOne(gsession_id);

        if (one == null) {
            result.put("error", true);
        } else {
            String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = nowDate + "-" + generateRandom(5);
            MimeType mimeType = MimeTypeUtils.parseMimeType(image.getMime());
            String fileExtension = mimeType.getSubtype();
            String url = "https://api.github.com/repos/Code-for-Korea/c4k-blog-front/contents/assets/img/posts/" +
                    nowDate + "/" + fileName + "." + fileExtension;

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("accept", "application/vnd.github.v3+json");
            headers.set("Authorization", "token " + one.getAccessToken());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "[NEW] " + fileName + "." + fileExtension);
            responseBody.put("content", image.getImageEncode());

            HttpEntity<Map> requestEntity = new HttpEntity<Map>(responseBody, headers);

            try {
                HttpEntity<Map> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.PUT, requestEntity, Map.class, responseBody);
                result.put("msg", responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                result.put("error", true);
                result.put("msg", e.getMessage());
                result.put("status", e.getStatusCode().value());
            }
        }
        return new JSONResponse(result);
    }

    @PostMapping("/delete-test")
    public Map<String, Object> DeleteTest() {
        HashMap<String, Object> result = new HashMap<String, Object>();


        String url = "https://api.github.com/repos/Code-for-Korea/c4k-blog-front/contents/test2.txt";

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/vnd.github.v3+json");
        headers.set("Authorization", "token c8b0c1ea13e9e4e2c667586fc698f8cf41b412e1");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "TEST");
        responseBody.put("sha", "d79621ca777e294e544143e5fca1980be2084b7a");

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(responseBody, headers);
        HttpEntity<Map> response = restTemplate.exchange(uri.toString(), HttpMethod.DELETE, requestEntity, Map.class, responseBody);


        result.put("header", response.getHeaders()); //헤더 정보 확인
        result.put("body", response.getBody()); //실제 데이터 정보 확인

        return result;
    }

    @GetMapping("/oauth")
    public void CodeToAccessToken(HttpServletResponse response, @RequestParam String code)
            throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://github.com/login/oauth/access_token";

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

        MultiValueMap<String, String> responseBody = new LinkedMultiValueMap<String, String>();
        responseBody.add("client_id", client_id);
        responseBody.add("client_secret", clientSecret);

        responseBody.add("code", code);

        HashMap resultMap = restTemplate.postForObject(uri.toString(), responseBody, HashMap.class);

        if (resultMap.get("error") == null) {
            Long saveId = sessionService.save(
                    Session.create(
                            resultMap.get("access_token").toString(),
                            resultMap.get("refresh_token").toString()
                    )
            );
            Session one = sessionService.findOne(saveId);

            Cookie cookieGID = new Cookie("GSESSIONID", one.getSessionId());
            cookieGID.setMaxAge(28800);
            cookieGID.setPath("/");
            response.addCookie(cookieGID);

            Cookie cookieREF = new Cookie("REFRESH_TOKEN", one.getRefreshToken());
            cookieREF.setMaxAge(15811200);
            cookieREF.setPath("/");
            response.addCookie(cookieREF);
        }
        response.sendRedirect("https://blog.codefor.kr/tabs/editor");
    }

    @PostMapping("/oauth")
    public JSONResponse RenewingAccessTokenWithRefreshToken(
            HttpServletResponse response,
            @CookieValue(value = "GSESSIONID", required = false) String gsession_id,
            @CookieValue(value = "REFRESH_TOKEN", required = false) String ref_token
    ) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://github.com/login/oauth/access_token";

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

        Session one = sessionService.findOne(gsession_id);

        MultiValueMap<String, String> responseBody = new LinkedMultiValueMap<String, String>();
        responseBody.add("client_id", client_id);
        responseBody.add("client_secret", clientSecret);
        responseBody.add("refresh_token", ref_token);
        responseBody.add("grant_type", "refresh_token");

        HashMap resultMap = restTemplate.postForObject(uri.toString(), responseBody, HashMap.class);

        HashMap<String, Object> result = new HashMap<>();
        if (resultMap.get("error") == null) {
            sessionService.renewAccessToken(
                    one.getId(),
                    resultMap.get("access_token").toString(),
                    resultMap.get("refresh_token").toString()
            );

            result.put("msg", "success");

            Cookie cookieGID = new Cookie("GSESSIONID", one.getSessionId());
            cookieGID.setMaxAge(28800);
            cookieGID.setPath("/");
            response.addCookie(cookieGID);

            Cookie cookieREF = new Cookie("REFRESH_TOKEN", one.getRefreshToken());
            cookieREF.setMaxAge(15811200);
            cookieREF.setPath("/");
            response.addCookie(cookieREF);
        } else {
            result.put("error", true);
            result.put("ret", resultMap);
        }
        return new JSONResponse(result);
    }

    @PostMapping("/authorize")
    public JSONResponse AuthenticateSession(
            HttpServletResponse response,
            @CookieValue(value = "GSESSIONID", required = false) String gsession_id,
            @CookieValue(value = "REFRESH_TOKEN", required = false) String ref_token
    ) {
        System.out.println(gsession_id);
        System.out.println(ref_token);
        HashMap<String, Object> result = new HashMap<>();

        if (gsession_id == null) {
            result.put("error", true);
        } else {
            RestTemplate restTemplate = new RestTemplate();

            Session one = sessionService.findOne(gsession_id);
            if (one != null && one.getRefreshToken().equals(ref_token)) {
                String url = "https://api.github.com/user";

                UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "token " + one.getAccessToken());

                HttpEntity<Map> requestEntity = new HttpEntity<Map>(headers);

                try {
                    HttpEntity<Map> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity, Map.class);
                    result.put("profile", responseEntity.getBody());
                } catch (HttpClientErrorException e) {
                    result.put("error", true);
                    Cookie cookieGID = new Cookie("GSESSIONID", null);
                    cookieGID.setMaxAge(0);
                    cookieGID.setPath("/");
                    response.addCookie(cookieGID);

                    Cookie cookieREF = new Cookie("REFRESH_TOKEN", null);
                    cookieREF.setMaxAge(0);
                    cookieREF.setPath("/");
                    response.addCookie(cookieREF);
                }
            } else {
                result.put("error", true);
                Cookie cookieGID = new Cookie("GSESSIONID", null);
                cookieGID.setMaxAge(0);
                cookieGID.setPath("/");
                response.addCookie(cookieGID);

                Cookie cookieREF = new Cookie("REFRESH_TOKEN", null);
                cookieREF.setMaxAge(0);
                cookieREF.setPath("/");
                response.addCookie(cookieREF);
            }
        }
        return new JSONResponse(result);
    }

    @DeleteMapping("/authorize")
    public JSONResponse DeAuthenticateSession(HttpServletResponse response) {
        Cookie cookieGID = new Cookie("GSESSIONID", null);
        cookieGID.setMaxAge(0);
        cookieGID.setPath("/");
        response.addCookie(cookieGID);

        Cookie cookieREF = new Cookie("REFRESH_TOKEN", null);
        cookieREF.setMaxAge(0);
        cookieREF.setPath("/");
        response.addCookie(cookieREF);

        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", "브라우저에 저장된 인증 정보가 삭제되었습니다.");
        return new JSONResponse(result);
    }
}
