package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
