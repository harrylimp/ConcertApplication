package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Seat {
    SeatRow _row;
    SeatNumber _number;

    public Seat() {

    }

    public Seat(SeatRow seatRow, SeatNumber seatNumber) {
        _row = seatRow;
        _number = seatNumber;
    }

    public SeatRow getRow() {
        return _row;
    }

    public SeatNumber getNumber() {
        return _number;
    }

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
