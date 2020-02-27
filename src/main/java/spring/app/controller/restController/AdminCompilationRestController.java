package spring.app.controller.restController;

import com.sun.org.apache.xpath.internal.operations.String;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.app.dto.SongCompilationDto;
import spring.app.model.SongCompilation;
import spring.app.service.abstraction.FileUploadService;
import spring.app.service.abstraction.GenreService;
import spring.app.service.abstraction.SongCompilationService;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/admin/compilation")
public class AdminCompilationRestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(AdminCompilationRestController.class);

    private final SongCompilationService songCompilationService;
    private final FileUploadService fileUploadService;
    private final GenreService genreService;

    @Autowired
    public AdminCompilationRestController(SongCompilationService songCompilationService,
                                          FileUploadService fileUploadService, GenreService genreService) {
        this.songCompilationService = songCompilationService;
        this.fileUploadService = fileUploadService;
        this.genreService = genreService;
    }

    @GetMapping
    public List<SongCompilation> getAllCompilation() {
        return songCompilationService.getAllSongCompilations();
    }

    @PostMapping("/update")
    public void updateCompilation(@ModelAttribute SongCompilationDto songCompilationDto) throws IOException {
        LOGGER.info("POST request '/api/admin/compilation/update' songCompilationDto ID = {}", songCompilationDto.getId());
        SongCompilation compilation = songCompilationService.getSongCompilationById(songCompilationDto.getId());
        LOGGER.info("Song compilation with ID = {} exists", compilation.getId());

        String coverName = uploadCover(songCompilationDto.getCover());
        if (coverName != null) {
            // Удалить текущую обложку, если была
            if (compilation.getCover() != null) {
                fileUploadService.eraseCurrentFile(compilation.getCover());
                LOGGER.info("Old cover deleted");
            }
            compilation.setCover(coverName);
            LOGGER.info("Cover updated for song compilation with ID = {}", compilation.getId());
        }

        compilation.setName(songCompilationDto.getName());
        LOGGER.info("Compilation Editing: name set -> {}", songCompilationDto.getName());
        compilation.setGenre(
                genreService.getByName(songCompilationDto.getGenre()));
        LOGGER.info("Compilation Editing: genre set -> {}", songCompilationDto.getGenre());

        songCompilationService.updateCompilation(compilation);
        LOGGER.info("Song compilation with ID = {} updated successfully", compilation.getId());
    }

    @PostMapping
    public void addCompilation(@ModelAttribute SongCompilationDto songCompilationDto) {
        LOGGER.info("POST request on '/api/admin/compilation/' to add a new SongCompilation with temporary name -> {}",
                songCompilationDto.getName());
        // creating and configuring new SongCompilation object
        SongCompilation songCompilation = new SongCompilation();
        songCompilation.setGenre(genreService.getByName(songCompilationDto.getName()));
        String

        songCompilationService.addSongCompilation(songCompilation);
        LOGGER.info("Song compilation with name -> {} added to DB", songCompilation.getName());
    }

    @DeleteMapping("/delete")
    public void deleteCompilation(@RequestBody Long id) throws IOException {
        LOGGER.info("DELETE request '/api/admin/compilation/delete' songCompilationDto ID = {}", id);
        songCompilationService.deleteSongCompilation(
                songCompilationService.getSongCompilationById(id));
        LOGGER.info("Song compilation with ID = {} deleted successfully", id);
    }

    private String uploadCover(MultipartFile file) throws IOException {
        String coverName = null;
        if (file != null && fileUploadService.isImage(file.getOriginalFilename())) {
            coverName = fileUploadService.upload(file);
            LOGGER.info("Cover uploaded. Cover name: {}", coverName);
        }

        return coverName;
    }
}
