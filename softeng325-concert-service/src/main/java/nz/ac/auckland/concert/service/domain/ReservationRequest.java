package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Embeddable
public class ReservationRequest {

    @Column(name="NO_SEATS")
    private int _numberOfSeats;

    @Column(name="SEAT_TYPE")
    private PriceBand _seatType;

    @ManyToOne
    @Column(name="CONCERT_ID")
    private Concert _concert;

    @Column(name="DATE")
    private LocalDateTime _date;

    public ReservationRequest() {}

    public ReservationRequest(int numberOfSeats, PriceBand seatType, Concert concert, LocalDateTime date) {
        _numberOfSeats = numberOfSeats;
        _seatType = seatType;
        _concert = concert;
        _date = date;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReservationRequest))
            return false;
        if (obj == this)
            return true;

        ReservationRequest rhs = (ReservationRequest) obj;
        return new EqualsBuilder().
                append(_numberOfSeats, rhs._numberOfSeats).
                append(_seatType, rhs._seatType).
                append(_concert, rhs._concert).
                append(_date, rhs._date).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_numberOfSeats).
                append(_seatType).
                append(_concert).
                append(_date).
                hashCode();
    }
}
