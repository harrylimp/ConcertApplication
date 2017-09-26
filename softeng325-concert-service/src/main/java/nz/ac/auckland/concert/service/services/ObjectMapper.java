package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.types.SeatStatus;
import nz.ac.auckland.concert.service.domain.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ObjectMapper {

    public static ConcertDTO concertToDTO(Concert concert) {
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

    public static PerformerDTO performerToDTO(Performer performer) {
        Set<Long> concertIDs = new HashSet<Long>();
        for (Concert concert : performer.getConcerts()) {
            concertIDs.add(concert.getId());
        }
        PerformerDTO performerDTO = new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                concertIDs
        );
        return performerDTO;
    }

    public static User userToDomainModel(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String firstname = userDTO.getFirstname();
        String lastname = userDTO.getLastname();
        User user = new User(username, password, firstname, lastname);
        return user;
    }

    public static UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getFirstname(),
                user.getLastname()
        );
        return userDTO;
    }

    public static SeatDTO seatToDTO(Seat seat) {
        SeatDTO seatDTO = new SeatDTO(
                seat.getRow(),
                seat.getNumber()
        );
        return seatDTO;
    }

    public static Seat seatToDomainModel(SeatDTO seatDTO, LocalDateTime dateTime, SeatStatus status) {
        Seat seat = new Seat(
                seatDTO.getRow(),
                seatDTO.getNumber(),
                dateTime,
                status
        );
        return seat;
    }

    public static CreditCard creditCardToDomainModel(CreditCardDTO creditCardDTO) {
        CreditCard.Type type;
        if (creditCardDTO.getType() == CreditCardDTO.Type.Visa) {
            type = CreditCard.Type.Visa;
        } else {
            type = CreditCard.Type.Master;
        }
        CreditCard creditCard = new CreditCard(
                type,
                creditCardDTO.getName(),
                creditCardDTO.getNumber(),
                creditCardDTO.getExpiryDate()
        );
        return creditCard;
    }

    public static BookingDTO bookingToDTO(Booking booking) {
        Set<SeatDTO> seatDTOs = new HashSet<SeatDTO>();
        for (Seat seat : booking.getSeats()) {
            seatDTOs.add(ObjectMapper.seatToDTO(seat));
        }
        BookingDTO bookingDTO = new BookingDTO(
                booking.getConcert().getId(),
                booking.getConcert().getTitle(),
                booking.getDateTime(),
                seatDTOs,
                booking.getPriceBand()
        );
        return bookingDTO;
    }
}
