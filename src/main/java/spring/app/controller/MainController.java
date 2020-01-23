package spring.app.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import spring.app.model.Company;
import spring.app.model.PlayList;
import spring.app.model.Role;
import spring.app.model.User;
import spring.app.service.abstraction.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

@Controller("/test")
public class MainController {
    private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final RoleService roleService;
    private final UserService userService;
    private final GenreService genreService;
    private final CompanyService companyService;
    private final OrgTypeService orgTypeService;
    private final PlayListService playListService;

    @Value("${googleRedirectUri}")
    private String googleRedirectUri;
    @Value("${googleClientId}")
    private String googleClientId;
    @Value("${googleResponseType}")
    private String googleResponseType;
    @Value("${googleScope}")
    private String googleScope;
    @Value("${googleClientSecret}")
    private String googleClientSecret;

    @Value("${vkAppId}")
    private String appId;
    @Value("${vkClientSecret}")
    private String clientSecret;
    @Value("${vkRedirectUri}")
    private String redirectUri;

    @Autowired
    public MainController(RoleService roleService, UserService userService, GenreService genreService, CompanyService companyService, OrgTypeService orgTypeService, PlayListService playListService) {
        this.roleService = roleService;
        this.userService = userService;
        this.genreService = genreService;
        this.companyService = companyService;
        this.orgTypeService = orgTypeService;
        this.playListService = playListService;
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String redirectToLoginPage() {
        return "redirect:/login";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }

    @RequestMapping(value = {"/login-captcha"}, method = RequestMethod.GET)
    public ModelAndView showLoginPageCaptcha() {
        return new ModelAndView("login-captcha");
    }

    @RequestMapping(value = {"/translation"}, method = RequestMethod.GET)
    public ModelAndView showPlayerPage() {

        return new ModelAndView("translation");
    }

    @RequestMapping(value = "/googleAuth")
    public String GoogleAuthorization() {

        StringBuilder url = new StringBuilder();
        url.append("https://accounts.google.com/o/oauth2/auth?redirect_uri=")
                .append(googleRedirectUri)
                .append("&response_type=")
                .append(googleResponseType)
                .append("&client_id=")
                .append(googleClientId)
                .append("&scope=")
                .append(googleScope);
        return "redirect:" + url.toString();
    }

    @RequestMapping(value = "/google")
    public String GoogleAuthorization(@RequestParam("code") String code) throws IOException {
        final HttpTransport transport = new NetHttpTransport();
        final JacksonFactory jsonFactory = new JacksonFactory();

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory,
                googleClientId, googleClientSecret, code, googleRedirectUri).execute();

        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String googleId = payload.getSubject();
        User user = userService.getUserByGoogleId(googleId);
        if (user == null) {
            Role role = roleService.getRoleById((long) 2);
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(role);
            user = new User(googleId, email, roleSet, true);
            userService.addUser(user);

            //здесь сетим дефолтную компанию
            Company company = new Company();
            String companyName = "Default(" + UUID.randomUUID().toString() + ")";
            company.setName(companyName);
            company.setStartTime(LocalTime.of(11, 0));
            company.setCloseTime(LocalTime.of(23, 0));
            company.setOrgType(orgTypeService.getOrgTypeById(1L));
            company.setUser(userService.getUserByGoogleId(googleId));

            //сетим утренний плейлист
            PlayList morningPlayList = new PlayList();
//            morningPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String morningPlaylistName = "morning(" + UUID.randomUUID().toString() + ")";
            morningPlayList.setName(morningPlaylistName);
            playListService.addPlayList(morningPlayList);
            Set<PlayList> morningPlaylistSet = new HashSet<>();
            morningPlaylistSet.add(playListService.getPlayListByName(morningPlaylistName));
            company.setMorningPlayList(morningPlaylistSet);

            //сетим дневной плейлист
            PlayList middayPlayList = new PlayList();
//            middayPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String middayPlaylistName = "midday(" + UUID.randomUUID().toString() + ")";
            middayPlayList.setName(middayPlaylistName);
            playListService.addPlayList(middayPlayList);
            Set<PlayList> middayPlaylistSet = new HashSet<>();
            middayPlaylistSet.add(playListService.getPlayListByName(middayPlaylistName));
            company.setMiddayPlayList(middayPlaylistSet);

            //сетим вечерний плейлист
            PlayList eveningPlayList = new PlayList();
//            eveningPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String eveningPlaylistName = "evening(" + UUID.randomUUID().toString() + ")";
            eveningPlayList.setName(eveningPlaylistName);
            playListService.addPlayList(eveningPlayList);
            Set<PlayList> eveningPlaylistSet = new HashSet<>();
            eveningPlaylistSet.add(playListService.getPlayListByName(eveningPlaylistName));
            company.setEveningPlayList(eveningPlaylistSet);

            companyService.addCompany(company);
            user.setCompany(companyService.getByCompanyName(companyName));

            user = userService.getUserByGoogleId(googleId);
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/user";
    }

    @GetMapping("/player")
    public String player() {
        return "player";
    }

    @GetMapping(value = "/vkAuth")
    public String vkAuthorization() {
        String url = "https://oauth.vk.com/authorize?client_id=" + appId +
                "&display=page&redirect_uri=" + redirectUri +
                "&scope=status,email&response_type=code&v=5.103";
        return "redirect:" + url;
    }

    @GetMapping(value = "/vkontakte")
    public String vkAuthorization(@RequestParam("code") String code) throws ClientException, ApiException {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(Integer.parseInt(appId), clientSecret, redirectUri, code)
                .execute();
        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        User user = userService.getUserByVkId(actor.getId());
        if (user == null) {
            Role role = roleService.getRoleById((long) 2);
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(role);
            List<UserXtrCounters> list = vk.users().get(actor).execute();
            user = new User(list.get(0).getId(),
                    new String(list.get(0).getFirstName().getBytes(StandardCharsets.UTF_8)),
                    new String(list.get(0).getLastName().getBytes(StandardCharsets.UTF_8)),
                    authResponse.getEmail(),
                    roleSet,
                    companyService.getById(1L),
                    true);
            userService.addUser(user);

            //здесь сетим дефолтную компанию
            Company company = new Company();
            String companyName = "Default(" + UUID.randomUUID().toString() + ")";
            company.setName(companyName);
            company.setStartTime(LocalTime.of(11, 0));
            company.setCloseTime(LocalTime.of(23, 0));
            company.setOrgType(orgTypeService.getOrgTypeById(1L));
            company.setUser(userService.getUserByVkId(actor.getId()));

            //сетим утренний плейлист
            PlayList morningPlayList = new PlayList();
//            morningPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String morningPlaylistName = "morning(" + UUID.randomUUID().toString() + ")";
            morningPlayList.setName(morningPlaylistName);
            playListService.addPlayList(morningPlayList);
            Set<PlayList> morningPlaylistSet = new HashSet<>();
            morningPlaylistSet.add(playListService.getPlayListByName(morningPlaylistName));
            company.setMorningPlayList(morningPlaylistSet);

            //сетим дневной плейлист
            PlayList middayPlayList = new PlayList();
//            middayPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String middayPlaylistName = "midday(" + UUID.randomUUID().toString() + ")";
            middayPlayList.setName(middayPlaylistName);
            playListService.addPlayList(middayPlayList);
            Set<PlayList> middayPlaylistSet = new HashSet<>();
            middayPlaylistSet.add(playListService.getPlayListByName(middayPlaylistName));
            company.setMiddayPlayList(middayPlaylistSet);

            //сетим вечерний плейлист
            PlayList eveningPlayList = new PlayList();
//            eveningPlayList.setCompanyId(userService.getUserByGoogleId(googleId).getCompany().getId());
            String eveningPlaylistName = "evening(" + UUID.randomUUID().toString() + ")";
            eveningPlayList.setName(eveningPlaylistName);
            playListService.addPlayList(eveningPlayList);
            Set<PlayList> eveningPlaylistSet = new HashSet<>();
            eveningPlaylistSet.add(playListService.getPlayListByName(eveningPlaylistName));
            company.setEveningPlayList(eveningPlaylistSet);

            companyService.addCompany(company);
            user.setCompany(companyService.getByCompanyName(companyName));

            user = userService.getUserByVkId(actor.getId());
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/user";
    }

    //метод добавляющий дефолтную компанию с плейлистами утро/день/вечер для вошедших через google и vk
    public Company createDefaultCompany (String id, String typeOfAuth, String companyName) {
        Integer userId = null;

        Company company = new Company();
        company.setName(companyName);
        company.setStartTime(LocalTime.of(11, 0));
        company.setCloseTime(LocalTime.of(23, 0));
        company.setOrgType(orgTypeService.getOrgTypeById(1L));
        if (typeOfAuth.equals("google")) {
            company.setUser(userService.getUserByGoogleId(id));
        }
        if (typeOfAuth.equals("vk")) {
            userId = Integer.parseInt(id);
            company.setUser(userService.getUserByVkId(userId));
        }
        //сетим утренний плейлист

        PlayList morningPlayList = new PlayList();
        morningPlayList.setName("morning");
        playListService.addPlayList(morningPlayList);

        Set<PlayList> morningPlaylistSet = new HashSet<>();
        morningPlaylistSet.add(morningPlayList);

        company.setMorningPlayList(morningPlaylistSet);

//        company.setMorningPlayList(new HashSet<>());
//        Set<PlayList> morningPlayListSet = company.getMorningPlayList();
//        morningPlayListSet.add(new PlayList("morning"));

        //сетим дневной плейлист
        Set<PlayList> middayPlaylistSet = new HashSet<>();
        PlayList middayPlayList = new PlayList();
        playListService.addPlayList(middayPlayList);
        middayPlaylistSet.add(playListService.getPlayList(2L));
        company.setMiddayPlayList(middayPlaylistSet);

//        company.setMiddayPlayList(new HashSet<>());
//        Set<PlayList> middayPlayListSet = company.getMiddayPlayList();
//        middayPlayListSet.add(new PlayList("midday"));

        //сетим вечерний плейлист
        Set<PlayList> eveningPlaylistSet = new HashSet<>();
        PlayList eveningPlayList = new PlayList();
        playListService.addPlayList(eveningPlayList);
        eveningPlaylistSet.add(playListService.getPlayList(3L));
        company.setEveningPlayList(eveningPlaylistSet);

//        company.setEveningPlayList(new HashSet<>());
//        Set<PlayList> eveningPlayListSet = company.getEveningPlayList();
//        eveningPlayListSet.add(new PlayList("evening"));

        return company;
    }
}