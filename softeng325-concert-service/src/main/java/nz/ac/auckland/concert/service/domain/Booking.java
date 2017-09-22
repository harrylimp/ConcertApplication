package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;

import java.time.LocalDateTime;
import java.util.Set;

public class Booking {
    private Concert _concert;
    private String _concertTitle;
    private LocalDateTime _dateTime;
    private Set<Seat> _seats;
    private PriceBand _priceBand;
}
