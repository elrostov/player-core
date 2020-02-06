package spring.app.service.impl.musicSearcher.serchServices;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import spring.app.service.abstraction.DownloadMusicService;
import spring.app.service.entity.Track;
import spring.app.util.PlayerPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Transactional
public class KrolikSaitServiceImpl implements DownloadMusicService {

    private RestTemplate restTemplate = new RestTemplate();
    private String authorName;
    private String songName;
    private String trackName;


    public String searchSong(String author, String song) throws IOException {

        final String url = "https://krolik.biz/search/?q=";
        Document document = null;
        String link = "";

        try {
            document = Jsoup.connect(String.format("%s%s %s", url, author, song)).get();

            Element first = document.getElementsByAttributeValue("class", "mp3").first();

            link = first.getElementsByClass("btn play").attr("data-url");
            author = first.getElementsByClass("artist_name").text();
            song = first.getElementsByClass("song_name").text();

            trackName = author + " – " + song;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error search on muzofond");
        }
        return link;
    }


    @Override
    public Track getSong(String author, String song) throws IOException {
        try {
            String link = searchSong(author, song);

            byte[] track = restTemplate.getForObject(link, byte[].class);

            if (track.length > 1024 * 20) {    //проверка что песня полноценная

                Path path = PlayerPaths.getSongsDir(trackName + ".mp3");
                if (path != null) {
                    try {
                        Files.write(path, track);  //записываем песню с директорию
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else return null;  //если песня меньше 2мб возвращаем 0
            return new Track(authorName, songName, trackName, track);
        } catch (IOException e) {
            System.out.println("Ошибка скачивания с krolik.biz");
        }
        return null;
    }
}