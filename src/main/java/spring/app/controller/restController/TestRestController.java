package spring.app.controller.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.app.model.Song;
import spring.app.service.abstraction.SongCompilationService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/test")
public class TestRestController {
    private SongCompilationService songCompilationService;

    @Autowired
    public TestRestController(SongCompilationService songCompilationService) {
        this.songCompilationService = songCompilationService;
    }

    @GetMapping("/compilation/content/{compilationId}")
    public List<Song> getAllWithGenreByGenreId(@PathVariable Long compilationId) {
        return songCompilationService.getSongCompilationContentById(compilationId);
    }

    @GetMapping("/compilation/content/available/{compilationId}")
    public List<Song> getAvailableWithGenreByGenreId(@PathVariable Long compilationId) {
        return songCompilationService.getAvailableSongsForCompilationById(compilationId);
    }

    @DeleteMapping("/compilation/{compilationId}/{songId}")
    public void removeSongFromSongCompilation(@PathVariable Long compilationId, @PathVariable Long songId) {
        songCompilationService.removeSongFromSongCompilation(compilationId, songId);
    }

}
