package spring.app.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "song")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Song {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Author.class)
    @JoinColumn(name = "author_id")
    private Author author;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Genre.class)
    @JoinColumn(name = "genre_id")
    private Genre genre;


    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    private Set<SongQueue> songQueues;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SongCompilation.class)
    @JoinTable(name = "song_compilation_on_song",
            joinColumns = {@JoinColumn(name = "song_id")},
            inverseJoinColumns = {@JoinColumn(name = "song_compilation_id")})
    private Set<SongCompilation> songCompilations;

    public Song() {
    }

    public Song(Long id, String name, Author author, Genre genre) {
    }

    public Song(Long id, String name, Author author, Genre genre, Set<SongQueue> songQueues) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.songQueues = songQueues;
    }

    public Song(Long id, String name, Genre genre) {
        this.id = id;
        this.name = name;
        this.genre = genre;
    }

    public Song(Long id, String name) {
        this.id = id;
        this.name = name;
    }

        public Song(String name, Author author, Genre genre) {
        this.name = name;
        this.author = author;
        this.genre = genre;
    }

    public Set<SongCompilation> getSongCompilations() {
        return songCompilations;
    }

    public void setSongCompilations(Set<SongCompilation> songCompilations) {
        this.songCompilations = songCompilations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Song(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Author getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Set<SongQueue> getSongQueues() {
        return songQueues;
    }

    public void setSongQueues(Set<SongQueue> songQueues) {
        this.songQueues = songQueues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (id != null ? !id.equals(song.id) : song.id != null) return false;
        return name != null ? !name.equals(song.name) : song.name != null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author=" + author +
                ", genre=" + genre +
                ", song=" + songQueues +
                '}';
    }
}
