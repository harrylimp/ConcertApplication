package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
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
                    NewCookie cookie = new NewCookie(newUser.getUsername(), newUser.getUUID());
                    response = Response.status(200).entity(ObjectMapper.userToDTO(newUser)).cookie(cookie);

                    em.getTransaction().commit();
                    em.close();
                }
            }
            return response.build();
        }
    }

    private NewCookie makeCookie(User user){

        NewCookie newCookie = new NewCookie(user.getUsername(), UUID.randomUUID().toString());
        return newCookie;

    }
}
