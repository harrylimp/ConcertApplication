package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

import java.util.HashSet;
import java.util.Set;

public class ConcertMapper {

    public static ConcertDTO toDTO(Concert concert) {
        Set<Long> performers = new HashSet<Long>();
        for (Performer performer : concert.getPerformers()) {
            performers.add(performer.getId());
        }

        ConcertDTO concertDTO = new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getDates(),
                concert.getTariff(),
                performers
        );
        return concertDTO;
    }

}
