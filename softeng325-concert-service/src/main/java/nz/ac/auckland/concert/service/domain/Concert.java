package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name="CONCERTS")
public class Concert {

    @Id
    @Column(name="CONCERT_ID")
    private Long _id;

    @Column(name="TITLE")
    private String _title;

    @ElementCollection
    @CollectionTable(
            name="CONCERT_DATES",
            joinColumns=@JoinColumn(name="CONCERT_ID"))
    @Column(name="DATE")
    private Set<LocalDateTime> _dates;

    @ElementCollection
    @CollectionTable(
            name="CONCERT_TARIFS",
            joinColumns=@JoinColumn(name="CONCERT_ID"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name="PRICEBAND")
    @Column(name="TOTAL_COST")
    private Map<PriceBand, BigDecimal> _tariff;

    @ManyToMany
    @JoinTable (
            name = "CONCERT_PERFORMER",
            joinColumns = @JoinColumn(name="CONCERT_ID"),
            inverseJoinColumns = @JoinColumn(name="PERFORMER_ID", nullable=false))
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

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public Set<LocalDateTime> getDates() {
        return _dates;
    }

    public void setDates(Set<LocalDateTime> _dates) {
        this._dates = _dates;
    }

    public Map<PriceBand, BigDecimal> getTariff() {
        return _tariff;
    }

    public void setTariff(Map<PriceBand, BigDecimal> _tariff) {
        this._tariff = _tariff;
    }

    public Set<Performer> getPerformers() {
        return _performers;
    }

    public void setPerformers(Set<Performer> _performers) {
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
                hashCode();
    }
}
