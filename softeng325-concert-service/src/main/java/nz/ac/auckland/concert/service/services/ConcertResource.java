package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.types.Config;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Path("/concerts")
public class ConcertResource {

    @GET
    @Path("/getConcerts")
    public Response getConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        em.getTransaction().begin();

        TypedQuery<Concert> concertQuery =
                em.createQuery("select c from Concert c", Concert.class);
        List<Concert> concerts = concertQuery.getResultList();

        Set<ConcertDTO> concertDTOs = new HashSet<ConcertDTO>();
        for (Concert concert : concerts) {
            concertDTOs.add(ConcertMapper.toDTO(concert));
        }

        GenericEntity<Set<ConcertDTO>> entity = new GenericEntity<Set<ConcertDTO>>(concertDTOs){};

        // Concert domain model to DTO back to the client in the entity
        em.getTransaction().commit();

        em.close();

        return Response.ok(entity).build();
    }

    @GET
    @Path("/getPerformers")
    public Response getPerformers() {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        em.getTransaction().begin();

        TypedQuery<Performer> performerQuery =
                em.createQuery("select p from Performer p", Performer.class);
        List<Performer> performers = performerQuery.getResultList();

        Set<PerformerDTO> performerDTOs = new HashSet<PerformerDTO>();
        for (Performer performer : performers) {
            performerDTOs.add(PerformerMapper.toDTO(performer));
        }

        GenericEntity<Set<PerformerDTO>> entity = new GenericEntity<Set<PerformerDTO>>(performerDTOs){};

        // Concert domain model to DTO back to the client in the entity
        em.getTransaction().commit();

        em.close();

        return Response.ok(entity).build();
    }

    @POST
    @Path("/createUser")
    public Response createUser(UserDTO userDTO) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String firstname = userDTO.getFirstname();
        String lastname = userDTO.getLastname();
        if (username == null || password == null || firstname == null || lastname == null) { // Check how others did it
            em.close();
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.CREATE_USER_WITH_MISSING_FIELDS)
                    .build());
        } else {
            ResponseBuilder response = null;
            User user = ObjectMapper.userToDomainModel(userDTO);
            em.getTransaction().begin();
            User newUser = em.find(User.class, user.getUsername());

            if (newUser == null) {
                // I also need to authenticate the user here
                NewCookie cookie = makeCookie(user);
                user.setUUID(cookie.getValue());

                em.persist(user);
                response = Response.status(201).entity(ObjectMapper.userToDTO(user)).cookie(cookie);

                em.getTransaction().commit();
                em.close();
            } else {
                em.close();
                throw new BadRequestException(Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
                        .build());
            }
            return response.build();
        }
    }

    @PUT
    @Path("/authenticateUser")
    public Response authenticateUser(UserDTO userDTO) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        if (username == null || password == null) {
            em.close();
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS) // Change message type
                    .build());
        } else {
            em.getTransaction().begin();
            ResponseBuilder response = null;
            User user = new User(username, password); // Change this implementation into ObjectMapper later
            User newUser = em.find(User.class, user.getUsername());

            if (newUser == null) {
                em.close();
                throw new BadRequestException(Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(Messages.AUTHENTICATE_NON_EXISTENT_USER) // Change message type
                        .build());
            } else {
                if (!newUser.getPassword().equals(userDTO.getPassword())) {
                    em.close();
                    throw new BadRequestException(Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD) // Change message type
                            .build());
                } else {
                    // We need to add authentication too somehow - does persisting the User automatically do this?
                    NewCookie cookie = new NewCookie(Config.CLIENT_COOKIE, newUser.getUUID());
                    response = Response.status(200).entity(ObjectMapper.userToDTO(newUser)).cookie(cookie);

                    em.getTransaction().commit();
                    em.close();
                }
            }
            return response.build();
        }
    }

    @PUT
    @Path("/reservationRequest")
    public Response reservationRequest(ReservationRequestDTO reservationRequestDTO, @CookieParam("clientId") Cookie clientId) {

        // Check if the datetime is relevant to the concert
        // Check if the number of seats are available

        EntityManager em = PersistenceManager.instance().createEntityManager();
        em.getTransaction().begin();

        System.out.println("HELLO?");

        String uuid = clientId.getValue();
        // CAN I DO THIS?
        if (uuid == null || uuid.length() == 0) {
            em.close();
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.UNAUTHENTICATED_REQUEST) // Change message type
                    .build());
        }

        System.out.println(uuid + "empty?");

        TypedQuery<User> userQuery =
                em.createQuery("select u from User u where u._uuid = :cookie ", User.class); // CHECK THIS QUERY
        userQuery.setParameter("cookie", uuid);

        List<User> users = userQuery.getResultList();
        if (users == null) {
            em.close();
            System.out.println(uuid);
            User user = users.get(0);
            System.out.println(user.getFirstname());
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.BAD_AUTHENTICATON_TOKEN)
                    .build());
        }

        System.out.println("HELLO??!!?");

        // Extract DTO fields
        int numberOfSeats = reservationRequestDTO.getNumberOfSeats();
        PriceBand priceBand = reservationRequestDTO.getSeatType();
        Long concertID = reservationRequestDTO.getConcertId();
        LocalDateTime dateTime = reservationRequestDTO.getDate();

        // NOT SURE IF DOING THESE CHECKS CORRECTLY
        if (numberOfSeats == 0 || priceBand == null || concertID == null || dateTime == null) {
            em.close();
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS)
                    .build());
        }

        // Making a query for the concert
        TypedQuery<Concert> concertQuery =
                em.createQuery("select c from Concert c where c.id = :concertID ", Concert.class);
        concertQuery.setParameter("concertID", concertID);
        List<Concert> concerts = concertQuery.getResultList();

        // This is valid right?
        Concert concert = concerts.get(0);
        if (concert == null) {
            // This concert does not exist
        } else {
            Boolean isDate = false;
            for (LocalDateTime date : concert.getDates()) {
                if (date.equals(dateTime)) { // CAN WE COMPARE LIKE THIS?
                    isDate = true;
                }
            }

            if (!isDate) {
                em.close();
                throw new BadRequestException(Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
                        .build());
            }
        }

        // THIS IS THE HARD PART!! Querying the SEATS TABLE!!!W

        return null;
    }

    private NewCookie makeCookie(User user){

        NewCookie newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
        return newCookie;

    }
}
