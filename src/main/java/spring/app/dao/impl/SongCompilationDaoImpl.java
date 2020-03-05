package spring.app.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.app.dao.abstraction.SongCompilationDao;
import spring.app.model.Song;
import spring.app.model.SongCompilation;

import javax.persistence.TypedQuery;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class SongCompilationDaoImpl extends AbstractDao<Long, SongCompilation> implements SongCompilationDao {

    SongCompilationDaoImpl() {
        super(SongCompilation.class);
    }

    @Override
    public List<SongCompilation> getListSongCompilationsByGenreId(Long id) {
        TypedQuery<SongCompilation> query = entityManager.createQuery(
                "SELECT sc FROM SongCompilation sc WHERE sc.genre.id = :id", SongCompilation.class);
        query.setParameter("id", id);
        List<SongCompilation> list = query.getResultList();
        return list;
    }

    // MySQL native --->
    // SELECT s.* FROM song s JOIN song_compilation_on_song scos ON s.id=scos.song_id WHERE scos.song_compilation_id=1;
    @Override
    public List<Song> getSongCompilationContentById(Long compilationId) {
        TypedQuery<Song> query = entityManager.createQuery(
                "SELECT s FROM Song s JOIN s.songCompilations sc WHERE sc.id = :id ", Song.class);
        query.setParameter("id", compilationId);

        return query.getResultList();
    }

    @Override
    public List<Song> getAvailableContentForCompilationById(SongCompilation songCompilation) {
        TypedQuery<Song> query = entityManager.createQuery(
                "SELECT s FROM Song s WHERE s.genre.id = :genreId AND s NOT IN " +
                        "(SELECT s FROM Song s JOIN s.songCompilations sc WHERE sc.id = :compilationId )", Song.class);
        query.setParameter("genreId", songCompilation.getGenre().getId());
        query.setParameter("compilationId", songCompilation.getId());

        return query.getResultList();
    }

    // MySQL native --->
    // DELETE FROM song_compilation_on_song WHERE song_compilation_id=x AND song_id=y;
    @Override
    public void removeSongFromSongCompilation(SongCompilation songCompilation, Song song) {
        songCompilation.getSong().remove(song);
        song.getSongCompilations().remove(songCompilation);
    }

    @Override
    public void deleteValByGenreId(Long id) {
        TypedQuery<SongCompilation> query = (TypedQuery<SongCompilation>) entityManager.createQuery("UPDATE SongCompilation SET genre_id = null WHERE genre_id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public SongCompilation getSongCompilationByCompilationName(String compilationName) {
        List<SongCompilation> songCompilationList = entityManager.createQuery("FROM SongCompilation WHERE name = :compilationName", SongCompilation.class)
                .setParameter("compilationName", compilationName)
                .getResultList();
        return songCompilationList.isEmpty() ? null : songCompilationList.get(0);
    }
}
