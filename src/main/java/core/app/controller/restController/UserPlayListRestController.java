package core.app.controller.restController;

import core.app.model.Company;
import core.app.model.SongCompilation;
import core.app.model.User;
import core.app.service.abstraction.SongCompilationService;
import core.app.service.abstraction.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/play-list")
public class UserPlayListRestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserPlayListRestController.class);
    private SongCompilationService songCompilationService;
    private UserService userService;

    @Autowired
    public UserPlayListRestController(SongCompilationService songCompilationService, UserService userService) {
        this.songCompilationService = songCompilationService;
        this.userService = userService;
    }

    @PostMapping(value = "/morning-playlist/add/song-compilation")
    public void addSongCompilationToMorningPlaylist(@RequestBody Long id) {
        LOGGER.info("POST request '/morning-playlist/add/song-compilation' for SongCompilationId = {}", id);
        songCompilationService.addSongCompilationToMorningPlaylist(id);
    }

    @DeleteMapping(value = "/{dayTime}-playlist/delete/song-compilation/{playlistId}")
    public void deleteSongCompilationFromPlaylist(@PathVariable("playlistId") Long id,
                                                  @PathVariable("dayTime") String dayTime) {
        LOGGER.info("DELETE request '/{}-playlist/delete/song-compilation/{}'", dayTime, id);
        songCompilationService.deleteSongCompilationFromPlayList(id, dayTime);
    }

    @PostMapping(value = "/midday-playlist/add/song-compilation")
    public void addSongCompilationToMiddayPlaylist(@RequestBody Long id) {
        LOGGER.info("POST request '/midday-playlist/add/song-compilation' for SongCompilationId = {}", id);
        songCompilationService.addSongCompilationToMiddayPlaylist(id);
    }

    @PostMapping(value = "/evening-playlist/add/song-compilation")
    public void addSongCompilationToEveningPlaylist(@RequestBody Long id) {
        LOGGER.info("POST request '/evening-playlist/add/song-compilation' for SongCompilationId = {}", id);
        songCompilationService.addSongCompilationToEveningPlaylist(id);
    }

    @GetMapping(value = "/morning-playlist/get/all-song-compilation")
    public List<SongCompilation> getAllCompilationsInMorningPlaylist(@AuthenticationPrincipal User user) {
        LOGGER.info("GET request '/morning-playlist/get/all-song-compilation' for User = {}", user);
        Company company = userService.getUserById(user.getId()).getCompany();
        return company == null ? null : songCompilationService.getAllCompilationsInMorningPlaylistByCompanyId(company.getId());
    }

    @GetMapping(value = "/midday-playlist/get/all-song-compilation")
    public List<SongCompilation> getAllCompilationsInMiddayPlaylist(@AuthenticationPrincipal User user) {
        LOGGER.info("GET request '/midday-playlist/get/all-song-compilation' for User = {}", user);
        Company company = userService.getUserById(user.getId()).getCompany();
        return company == null ? null : songCompilationService.getAllCompilationsInMiddayPlaylistByCompanyId(company.getId());
    }

    @GetMapping(value = "/evening-playlist/get/all-song-compilation")
    public List<SongCompilation> getAllCompilationsInEveningPlaylist(@AuthenticationPrincipal User user) {
        LOGGER.info("GET request '/evening-playlist/get/all-song-compilation' for User = {}", user);
        Company company = userService.getUserById(user.getId()).getCompany();
        return company == null ? null : songCompilationService.getAllCompilationsInEveningPlaylistByCompanyId(company.getId());
    }
}
