package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/subscription")
public class SubscriptionResource {

    protected Map<String, AsyncResponse> responseMap = new HashMap<String, AsyncResponse>();

    @GET
    @Path("/subscribe")
    public void subscribe(@Suspended AsyncResponse response, @CookieParam("clientId") Cookie clientId, @CookieParam("newsItem") Cookie newsItem) {

        EntityManager em = PersistenceManager.instance().createEntityManager();

        em.getTransaction().begin();

        LocalDateTime lastTime;
        if (clientId == null) {
            String time = newsItem.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            lastTime = LocalDateTime.parse(time, formatter);

            TypedQuery<NewsItem> newsItemTypedQuery =
                    em.createQuery("select n from NewsItem n where n._timestamp > :lastTime order by n._timestamp", NewsItem.class);
            newsItemTypedQuery.setParameter("lastTime", lastTime);
            List<NewsItem> newsItems = newsItemTypedQuery.getResultList();

            if (newsItems.size() != 0) {
                List<NewsItemDTO> newsItemDTOs = new ArrayList<>();
                for (int i = 0; i < newsItems.size(); i++) {
                    newsItemDTOs.add(ObjectMapper.newsItemToDTO(newsItems.get(0)));
                }
                response.resume(newsItemDTOs);
            }

        }
        responseMap.put(clientId.getValue(), response);
    }

    @GET
    @Path("/unSubscribe")
    public void unsubscribe(@CookieParam("clientId") Cookie clientId) {
        responseMap.remove(clientId.getValue());
    }

    @POST
    public void send(NewsItem newsItem) {
        for (AsyncResponse response : responseMap.values()) {
            response.resume(ObjectMapper.newsItemToDTO(newsItem));
        }
        responseMap.clear();
    }


}
