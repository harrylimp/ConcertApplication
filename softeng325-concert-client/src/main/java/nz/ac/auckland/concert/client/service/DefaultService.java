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

    private static String _cookie;

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
                if(cookies.containsKey(userDTO.getUsername())) {
                    _cookie = cookies.get(userDTO.getUsername()).getValue();
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
                if(cookies.containsKey(userDTO.getUsername())) {
                    _cookie = cookies.get(userDTO.getUsername()).getValue();
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

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

}
