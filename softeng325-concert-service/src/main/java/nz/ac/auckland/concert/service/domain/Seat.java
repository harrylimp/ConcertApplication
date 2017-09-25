package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.jaxb.LocalDateAdapter;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.types.SeatStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="SEATS")
public class Seat implements Serializable {

    @Id
    @Column(name="SEAT_ROW")
    SeatRow _row;

    @Id
    @Column(name="SEAT_NUMBER")
    SeatNumber _number;

    @Id
    @Column(name="DATES")
    private LocalDateTime _dateTime;

    @Column(name="SEAT_STATUS")
    private SeatStatus _status;

    public Seat() {

    }

    public Seat(SeatRow seatRow, SeatNumber seatNumber) {
        _row = seatRow;
        _number = seatNumber;
    }

    public Seat(SeatRow seatRow, SeatNumber seatNumber, LocalDateTime dateTime, SeatStatus status) {
        _row = seatRow;
        _number = seatNumber;
        _dateTime = dateTime;
        _status = status;
    }

    public SeatRow getRow() {
        return _row;
    }

    public SeatNumber getNumber() {
        return _number;
    }

    public LocalDateTime getDateTime() { return _dateTime; }

    public SeatStatus getStatus() { return _status; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Seat))
            return false;
        if (obj == this)
            return true;

        Seat rhs = (Seat) obj;
        return new EqualsBuilder().
                append(_row, rhs._row).
                append(_number, rhs._number).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_row).
                append(_number).
                hashCode();
    }

    @Override
    public String toString() {
        return _row + _number.toString();
    }
}
