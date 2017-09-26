package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name="RESERVATIONS")
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name="RESERVATION_ID")
    private Long _id;

    @Column(name="NO_SEATS")
    private int _numberOfSeats;

    @Column(name="SEAT_TYPE")
    private PriceBand _seatType;

    @ManyToOne
    private Concert _concert;

    @Column(name="DATE")
    private LocalDateTime _date;

    @OneToMany
    private Set<Seat> _seats;

    @Column
    private Timestamp _timeStamp;

    public Reservation() {}

    public Reservation(int numberOfSeats, PriceBand seatType, Concert concert, LocalDateTime date, Set<Seat> seats, Timestamp timestamp) {
        _numberOfSeats = numberOfSeats;
        _concert = concert;
        _seatType = seatType;
        _date = date;
        _seats = seats;
        _timeStamp = timestamp;
    }

    public int getNumberOfSeats() {
        return _numberOfSeats;
    }

    public PriceBand getSeatType() {
        return _seatType;
    }

    public Concert getConcert() {
        return _concert;
    }

    public LocalDateTime getDate() {
        return _date;
    }

    public Long getId() {
        return _id;
    }

    public Set<Seat> getSeats() {
        return Collections.unmodifiableSet(_seats);
    }

    public Timestamp getTime() { return _timeStamp; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reservation))
            return false;
        if (obj == this)
            return true;

        Reservation rhs = (Reservation) obj;
        return new EqualsBuilder().
                append(_numberOfSeats, rhs._numberOfSeats).
                append(_seatType, rhs._seatType).
                append(_concert, rhs._concert).
                append(_date, rhs._date).
                append(_seats, rhs._seats).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_numberOfSeats).
                append(_seatType).
                append(_concert).
                append(_date).
                append(_seats).
                hashCode();
    }
}
