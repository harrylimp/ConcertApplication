package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class ReservationRequest {

    @Column(name="NO_SEATS")
    private int _numberOfSeats;

    @Column(name="SEAT_TYPE")
    private PriceBand _seatType;

    @Column(name="CONCERT_ID")
    private Long _concertID;

    @Column(name="DATE")
    private LocalDateTime _date;

    public ReservationRequest() {}

    public ReservationRequest(int numberOfSeats, PriceBand seatType, Long concertID, LocalDateTime date) {
        _numberOfSeats = numberOfSeats;
        _seatType = seatType;
        _concertID = concertID;
        _date = date;
    }

    public int getNumberOfSeats() {
        return _numberOfSeats;
    }

    public PriceBand getSeatType() {
        return _seatType;
    }

    public Long getConcertId() {
        return _concertID;
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
                append(_concertID, rhs._concertID).
                append(_date, rhs._date).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_numberOfSeats).
                append(_seatType).
                append(_concertID).
                append(_date).
                hashCode();
    }
}
