package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.service.domain.NewsItem;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/subscription")
public class SubscriptionResource {

    protected List<AsyncResponse> responses = new ArrayList<AsyncResponse>();

    @GET
    @Path("/subscribe")
    public void subscribe(@Suspended AsyncResponse response, @CookieParam("clientId") Cookie clientId) {
        String time = clientId.getValue();
        responses.add(response);
    }

    @POST
    public void send(NewsItem newsItem) {
        for (AsyncResponse response : responses) {
            response.resume(ObjectMapper.newsItemToDTO(newsItem));
        }
        responses.clear();
    }


}
