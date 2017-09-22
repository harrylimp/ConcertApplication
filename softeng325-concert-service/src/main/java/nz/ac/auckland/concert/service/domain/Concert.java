package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
public class Concert {

    @Id
    @GeneratedValue
    private Long _id;

    private String _title;
    private Set<LocalDateTime> _dates;
    private Map<PriceBand, BigDecimal> _tariff;
    private Set<Performer> _performers;

    public Concert() {

    }

    public Concert(Long id, String title, Set<LocalDateTime> dates, Map<PriceBand, BigDecimal> tariff, Set<Performer> performers) {
        _id = id;
        _title = title;
        _dates = dates;
        _tariff = tariff;
        _performers = performers;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public Set<LocalDateTime> get_dates() {
        return _dates;
    }

    public void set_dates(Set<LocalDateTime> _dates) {
        this._dates = _dates;
    }

    public Map<PriceBand, BigDecimal> get_tariff() {
        return _tariff;
    }

    public void set_tariff(Map<PriceBand, BigDecimal> _tariff) {
        this._tariff = _tariff;
    }

    public Set<Performer> get_performers() {
        return _performers;
    }

    public void set_performers(Set<Performer> _performers) {
        this._performers = _performers;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
                append(_title, rhs._title).
                append(_dates, rhs._dates).
                append(_tariff, rhs._tariff).
                append(_performers, rhs._performers).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_title).
                append(_dates).
                append(_tariff).
                append(_performers).
                hashCode();
    }
}
