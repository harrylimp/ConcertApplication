package nz.ac.auckland.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="RESERVATIONS")
public class Reservation {

    @Id
    @Column(name="RESERVATION_ID")
    private Long _id;

    private ReservationRequest _request;

    @OneToMany
    private Set<Seat> _seats;

    public Reservation() {}

    public Reservation(Long id, ReservationRequest request, Set<Seat> seats) {
        _id = id;
        _request = request;
        _seats = new HashSet<Seat>(seats);
    }

    public Long getId() {
        return _id;
    }

    public ReservationRequest getReservationRequest() {
        return _request;
    }

    public Set<Seat> getSeats() {
        return Collections.unmodifiableSet(_seats);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reservation))
            return false;
        if (obj == this)
            return true;

        Reservation rhs = (Reservation) obj;
        return new EqualsBuilder().
                append(_request, rhs._request).
                append(_seats, rhs._seats).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_request).
                append(_seats).
                hashCode();
    }
}
