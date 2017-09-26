package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.types.SeatStatus;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Seat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Application;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	private Set<Object> _singletons = new HashSet<Object>();
	private Set<Class<?>> _classes = new HashSet<Class<?>>();

	public ConcertApplication() {
		_singletons.add(new PersistenceManager());
		_classes.add(ConcertResource.class);

		EntityManager em = null;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			// Delete all existing entities of some type
			em.createQuery("delete from Seat s").executeUpdate();
			em.createQuery("delete from User u").executeUpdate();

			TypedQuery<Concert> concertQuery =
					em.createQuery("select c from Concert c", Concert.class);

			// Make many entities of some type
			for (Concert concert : concertQuery.getResultList()) {
				for (LocalDateTime dateTime : concert.getDates()) {
					for (SeatRow seatRow : SeatRow.values()) {
						for (int i = 0; i < TheatreLayout.getNumberOfSeatsForRow(seatRow); i++) {
							// Populate the number of seats here
							Seat seat = new Seat(seatRow, new SeatNumber(i+1), dateTime, SeatStatus.Empty);
							em.persist(seat);
						}
					}
				}
			}
			em.flush();
			em.clear();
			em.getTransaction().commit();

		} catch (Exception e) {
			// Process and log the exception
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	@Override
	public Set<Object> getSingletons() {
		return _singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}
}
