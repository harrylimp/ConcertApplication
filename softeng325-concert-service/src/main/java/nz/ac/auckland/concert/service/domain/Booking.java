package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@Table(name="BOOKINGS")
public class Booking {

    @Column(name="CONCERT_ID")
    private Concert _concert;

    @Column(name="CONCERT_TITLE")
    private String _concertTitle;

    @Column(name="DATE")
    private LocalDateTime _dateTime;

    @ElementCollection
    private Set<Seat> _seats;

    @Column(name="GENRE")
    @Enumerated(EnumType.STRING)
    private PriceBand _priceBand;

    public Booking() {
    }

    public Booking(Concert concert, String concertTitle,
                      LocalDateTime dateTime, Set<Seat> seats, PriceBand priceBand) {
        _concert = concert;
        _concertTitle = concertTitle;
        _dateTime = dateTime;

        _seats = new HashSet<Seat>();
        _seats.addAll(seats);

        _priceBand = priceBand;
    }

    public Concert getConcertId() {
        return _concert;
    }

    public String getConcertTitle() {
        return _concertTitle;
    }

    public LocalDateTime getDateTime() {
        return _dateTime;
    }

    public Set<Seat> getSeats() {
        return Collections.unmodifiableSet(_seats);
    }

    public PriceBand getPriceBand() {
        return _priceBand;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Seat))
            return false;
        if (obj == this)
            return true;

        Booking rhs = (Booking) obj;
        return new EqualsBuilder().append(_concert, rhs._concert)
                .append(_concertTitle, rhs._concertTitle)
                .append(_dateTime, rhs._dateTime)
                .append(_seats, rhs._seats)
                .append(_priceBand, rhs._priceBand).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(_concert)
                .append(_concertTitle).append(_dateTime).append(_seats)
                .append(_priceBand).hashCode();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("concert: ");
        buffer.append(_concertTitle);
        buffer.append(", date/time ");
        buffer.append(_seats.size());
        buffer.append(" ");
        buffer.append(_priceBand);
        buffer.append(" seats.");
        return buffer.toString();
    }
}
