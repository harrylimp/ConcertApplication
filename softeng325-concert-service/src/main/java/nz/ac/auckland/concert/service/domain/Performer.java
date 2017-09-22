package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.types.Genre;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public Performer(Long id, Genre genre, String name, String imageName, Set<Concert> concerts) {
        _id = id;
        _genre = genre;
        _name = name;
        _imageName = imageName;
        _concerts = concerts;
    }

    public Long getId() {
        return _id;
    }

    public Genre getGenre() {
        return _genre;
    }

    public String getName() {
        return _name;
    }

    public String getImageName() {
        return _imageName;
    }

    public Set<Concert> getConcerts() {
        return _concerts;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Performer))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
                append(_name, rhs._name).
                append(_imageName, rhs._imageName).
                append(_genre, rhs._genre).
                append(_concerts, rhs._concerts).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_name).
                append(_imageName).
                append(_genre).
                append(_concerts).
                hashCode();
    }
}
