package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.Genre;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class Performer {

    @Id
    @GeneratedValue
    private Long _id;

    private Genre _genre;
    private String _name;
    private String _imageName;
    private Set<Concert> _concerts;

    public Performer() {

    }

    public Performer(Long id, Genre genre, String name, String imageName) {
        _id = id;
        _genre = genre;
        _name = name;
        _imageName = imageName;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public Genre getGenre() {
        return _genre;
    }

    public void setGenre(Genre genre) {
        _genre = genre;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = _name;
    }

    public String getImageName() {
        return _imageName;
    }

    public void setImageName(String imageName) {
        _imageName = imageName;
    }

    public Set<Concert> get_concerts() {
        return _concerts;
    }

    public void set_imageName(Set<Concert> concerts) {
        _concerts = concerts;
    }
}
