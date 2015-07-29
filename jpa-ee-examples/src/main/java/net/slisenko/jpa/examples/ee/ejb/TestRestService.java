package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.Member;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/")
@Stateless
public class TestRestService {

    @EJB
    private AnotherBean anotherBean;

    @GET
    @Path("/test")
    @Produces({"application/json"})
    public List<Member> hello() {
        return anotherBean.getMembers();
    }
}