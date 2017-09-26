package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.types.Config;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.services.ConcertMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.ws.Service;

public class DefaultService implements ConcertService {

	private static Client _client;

    private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";

    private String _cookie;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response response = null;
        Set<ConcertDTO> concerts = null;
		try {
			_client = ClientBuilder.newClient();
			Builder builder = _client.target(WEB_SERVICE_URI + "/getConcerts").request().accept(MediaType.APPLICATION_XML);
			response = builder.get();

			concerts = response.readEntity(new GenericType<Set<ConcertDTO>>(){});

		} catch (Exception e) {
		} finally {
			_client.close();
		}
		return concerts;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
        Response response = null;
        Set<PerformerDTO> performers = null;

        try {
            _client = ClientBuilder.newClient();
            Builder builder = _client.target(WEB_SERVICE_URI + "/getPerformers").request();
            response = builder.get();

            performers = response.readEntity(new GenericType<Set<PerformerDTO>>(){});
        } catch (Exception e) {

        } finally {
            _client.close();
        }
        return performers;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		Response response = null;
		UserDTO userDTO = null;

		_client = ClientBuilder.newClient();
		Builder builder = _client.target(WEB_SERVICE_URI + "/createUser").request().accept(MediaType.APPLICATION_XML);
		response = builder.post(Entity.entity(newUser, MediaType.APPLICATION_XML));

		int responseCode = response.getStatus();
		switch(responseCode) {
			case 400:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 201: // CREATED
				userDTO = response.readEntity(UserDTO.class);

                Map<String, NewCookie> cookies = response.getCookies();
                if(cookies.containsKey(Config.CLIENT_COOKIE)) {
                    _cookie = cookies.get(Config.CLIENT_COOKIE).getValue();
                }
		}
		_client.close();
		return userDTO;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
        Response response = null;
        UserDTO userDTO = null;

        _client = ClientBuilder.newClient();
        Builder builder = _client.target(WEB_SERVICE_URI + "/authenticateUser").request().accept(MediaType.APPLICATION_XML);
        response = builder.put(Entity.entity(user, MediaType.APPLICATION_XML));

        int responseCode = response.getStatus();
        switch(responseCode) {
            case 400:
                String errorMessage = response.readEntity(String.class);
                throw new ServiceException(errorMessage);
            case 200: // CREATED
                userDTO = response.readEntity(UserDTO.class);

                Map<String, NewCookie> cookies = response.getCookies();
                if(cookies.containsKey(Config.CLIENT_COOKIE)) {
                    _cookie = cookies.get(Config.CLIENT_COOKIE).getValue();
                }

        }
        _client.close();
        return userDTO;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}


/**
 * Attempts to reserve seats for a concert. The reservation is valid for a
 * short period that is determine by the remote service.
 *
 * @param reservationRequest a description of the reservation, including
 * number of seats, price band, concert identifier, and concert date. All
 * fields are expected to be filled.
 *
 * @return a ReservationDTO object that describes the reservation. This
 * includes the original ReservationDTO parameter plus the seats (a Set of
 * SeatDTO objects) that have been reserved.
 */
 @Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {

	 Response response = null;
	 ReservationDTO reservationDTO = null;

	 _client = ClientBuilder.newClient();
	 Builder builder = _client.target(WEB_SERVICE_URI + "/reservationRequest").request().accept(MediaType.APPLICATION_XML);
	 builder.cookie(Config.CLIENT_COOKIE, _cookie);
	 response = builder.put(Entity.entity(reservationRequest, MediaType.APPLICATION_XML));

	 int responseCode = response.getStatus();
	 // Reservation that includes all the fields

	 switch(responseCode) {
		 case 400:
			 String errorMessage = response.readEntity(String.class);
			 throw new ServiceException(errorMessage);
		 case 200: // CREATED
			 reservationDTO = response.readEntity(ReservationDTO.class);

	 }
	 _client.close();
	 return reservationDTO;
	}

	/**
	 * Confirms a reservation. Prior to calling this method, a successful
	 * reservation request should have been made via a call to reserveSeats(),
	 * returning a ReservationDTO.
	 *
	 * @param reservation a description of the reservation to confirm.
	 *
	 * @throws ServiceException in response to any of the following conditions.
	 * The exception's message is defined in
	 * class nz.ac.auckland.concert.common.Messages.
	 *
	 * Condition: the request is made by an unauthenticated user.
	 * Messages.UNAUTHENTICATED_REQUEST
	 *
	 * Condition: the request includes an authentication token but it's not
	 * recognised by the remote service.
	 * Messages.BAD_AUTHENTICATON_TOKEN
	 *
	 * Condition: the reservation has expired.
	 * Messages.EXPIRED_RESERVATION
	 *
	 * Condition: the user associated with the request doesn't have a credit
	 * card registered with the remote service.
	 * Messages.CREDIT_CARD_NOT_REGISTERED
	 *
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 *
	 */
	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {

		Response response = null;

		_client = ClientBuilder.newClient();
		Builder builder = _client.target(WEB_SERVICE_URI + "/confirmReservation").request().accept(MediaType.APPLICATION_XML);
		builder.cookie(Config.CLIENT_COOKIE, _cookie);
		response = builder.put(Entity.entity(reservation, MediaType.APPLICATION_XML));

		int responseCode = response.getStatus();
		// Reservation that includes all the fields

		switch(responseCode) {
			case 400:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 200:
		}
		_client.close();
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Response response = null;

        _client = ClientBuilder.newClient();
        Builder builder = _client.target(WEB_SERVICE_URI + "/registerCreditCard").request().accept(MediaType.APPLICATION_XML);
        builder.cookie(Config.CLIENT_COOKIE, _cookie);
        response = builder.post(Entity.entity(creditCard, MediaType.APPLICATION_XML));

        int responseCode = response.getStatus();
        // Reservation that includes all the fields

        switch(responseCode) {
            case 400:
                String errorMessage = response.readEntity(String.class);
                throw new ServiceException(errorMessage);
        }
        _client.close();
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub

		// Need to get back all the Bookings of something

		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {

		// Some sort of subscription application

		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

}
