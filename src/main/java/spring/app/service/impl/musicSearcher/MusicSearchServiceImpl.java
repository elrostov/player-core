package spring.app.service.impl.musicSearcher;


import org.springframework.stereotype.Service;
import spring.app.service.abstraction.*;
import spring.app.service.entity.Track;
import spring.app.service.impl.musicSearcher.serchServices.DownloadMusicVkRuServiceImpl;
import spring.app.service.impl.musicSearcher.serchServices.KrolikSaitServiceImpl;
import spring.app.service.impl.musicSearcher.serchServices.MuzofondfmMusicSearchImpl;
import spring.app.service.impl.musicSearcher.serchServices.ZaycevSaitServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * класс для поиска песен по разным сервисам, в зависимости от результата
 */
@Service
@Transactional
public class MusicSearchServiceImpl implements MusicSearchService {

    private Track track;
    private GenreDefinerService genreDefiner;
    private DataUpdateService dataUpdater;
    private DownloadMusicVkRuServiceImpl vkMusic;
    private KrolikSaitServiceImpl krolikMusic;
    private ZaycevSaitServiceImpl zaycevMusic;
    private MuzofondfmMusicSearchImpl muzofondMusic;

    public MusicSearchServiceImpl(DownloadMusicVkRuServiceImpl vkMusic,
                                  ZaycevSaitServiceImpl zaycevMusic,
                                  MuzofondfmMusicSearchImpl muzofondMusic,
                                  KrolikSaitServiceImpl krolikMusic,
                                  GenreDefinerService genreDefiner,
                                  DataUpdateService dataUpdater) throws IOException {
        this.vkMusic = vkMusic;
        this.zaycevMusic = zaycevMusic;
        this.muzofondMusic = muzofondMusic;
        this.krolikMusic = krolikMusic;
        this.genreDefiner = genreDefiner;
        this.dataUpdater = dataUpdater;
        this.genreDefiner = genreDefiner;
        this.dataUpdater = dataUpdater;
    }

    @Override
    public Track getSong(String author, String song) throws IOException {
        List<? extends DownloadMusicService> listServices = new ArrayList<>(Arrays.asList(zaycevMusic, vkMusic, krolikMusic, muzofondMusic));
        for (DownloadMusicService service : listServices) {
            track = service.getSong(author, song);
            if(track != null) break;
        }
        return track;
    }


    //определяет жанр песни
    public String[] getGenre(String trackName) throws IOException {
        return genreDefiner.defineGenre(trackName);
    }
    //заносит данные скачаной песни в бд и возвращает id песни
    public Long updateData(Track track) throws IOException {
        String[] genreNames = getGenre(track.getFullTrackName());
        return dataUpdater.updateData(track.getAuthor(), track.getSong(), genreNames);
    }

}