package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.types.Config;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public class DefaultService implements ConcertService {

	private static Client _client;

    private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";

    private String _cookie;

    // AWS S3 access credentials for concert images.
    private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";

	private static final String AWS_SECRET_ACCESS_KEY = "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";

    // Name of the S3 bucket that stores images.
    private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

    // Download directory - a directory named "images" in the user's home
    // directory.
    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");
    private static final String USER_DIRECTORY = System
            .getProperty("user.home");
    private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
            + FILE_SEPARATOR + "images";

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Set<ConcertDTO> concerts = null;
		try {
			_client = ClientBuilder.newClient();
			Builder builder = _client.target(WEB_SERVICE_URI + "/getConcerts").request().accept(MediaType.APPLICATION_XML);
			Response response = builder.get();

			if (response.getStatus() == 200) {
				concerts = response.readEntity(new GenericType<Set<ConcertDTO>>(){});
			}
		} catch (Exception e) {
		} finally {
			_client.close();
		}
		return concerts;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
        Set<PerformerDTO> performers = null;

        try {
            _client = ClientBuilder.newClient();
            Builder builder = _client.target(WEB_SERVICE_URI + "/getPerformers").request();
            Response response = builder.get();

            if (response.getStatus() == 200) {
				performers = response.readEntity(new GenericType<Set<PerformerDTO>>() {
				});
			}
		} catch (Exception e) {
        } finally {
            _client.close();
        }
        return performers;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		UserDTO userDTO = null;

		_client = ClientBuilder.newClient();
		Builder builder = _client.target(WEB_SERVICE_URI + "/createUser").request().accept(MediaType.APPLICATION_XML);
		Response response = builder.post(Entity.entity(newUser, MediaType.APPLICATION_XML));

		int responseCode = response.getStatus();
		switch(responseCode) {
			case 400:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 201:
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
        UserDTO userDTO = null;

        _client = ClientBuilder.newClient();
        Builder builder = _client.target(WEB_SERVICE_URI + "/authenticateUser").request().accept(MediaType.APPLICATION_XML);
        Response response = builder.put(Entity.entity(user, MediaType.APPLICATION_XML));

        int responseCode = response.getStatus();
        switch(responseCode) {
            case 400:
                String errorMessage = response.readEntity(String.class);
                throw new ServiceException(errorMessage);
            case 200:
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

        _client = ClientBuilder.newClient();
        Builder builder = _client.target(WEB_SERVICE_URI + "/getPerformerImage").request().accept(MediaType.APPLICATION_XML);
        Response response = builder.put(Entity.entity(performer, MediaType.APPLICATION_XML));

        String imageName = response.readEntity(String.class);

        // Create download directory if it doesn't already exist.
        File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
        downloadDirectory.mkdir();

        // Create an AmazonS3 object that represents a connection with the
        // remote S3 service.
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .withCredentials(
                        new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        // Download the image.
        return download(s3, imageName);
	}

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

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {

		_client = ClientBuilder.newClient();
		Builder builder = _client.target(WEB_SERVICE_URI + "/confirmReservation").request().accept(MediaType.APPLICATION_XML);
		builder.cookie(Config.CLIENT_COOKIE, _cookie);
		Response response = builder.put(Entity.entity(reservation, MediaType.APPLICATION_XML));

		int responseCode = response.getStatus();

		switch(responseCode) {
			case 400:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
		}
		_client.close();
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		_client = ClientBuilder.newClient();
        Builder builder = _client.target(WEB_SERVICE_URI + "/registerCreditCard").request().accept(MediaType.APPLICATION_XML);
        builder.cookie(Config.CLIENT_COOKIE, _cookie);
        Response response = builder.post(Entity.entity(creditCard, MediaType.APPLICATION_XML));

        int responseCode = response.getStatus();

        switch(responseCode) {
            case 400:
                String errorMessage = response.readEntity(String.class);
                throw new ServiceException(errorMessage);
        }
        _client.close();
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		_client = ClientBuilder.newClient();
		Builder builder = _client.target(WEB_SERVICE_URI + "/getBookings").request().accept(MediaType.APPLICATION_XML);
		builder.cookie(Config.CLIENT_COOKIE, _cookie);
		Response response = builder.get();

		int responseCode = response.getStatus();

		Set<BookingDTO> bookedSeats = null;
		switch(responseCode) {
			case 400:
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			case 200:
				bookedSeats = response.readEntity(new GenericType<Set<BookingDTO>>(){});
		}
		_client.close();
		return bookedSeats;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {

		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

    private static Image download(AmazonS3 s3, String imageName) {
        try {
            S3Object o = s3.getObject(AWS_BUCKET, imageName);
            S3ObjectInputStream s3is = o.getObjectContent();
            BufferedImage img;
            img = ImageIO.read(s3is);
            s3is.close();
            return img;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
