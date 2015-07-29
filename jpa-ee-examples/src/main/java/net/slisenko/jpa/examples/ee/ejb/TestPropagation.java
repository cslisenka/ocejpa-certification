package net.slisenko.jpa.examples.ee.ejb;

import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/tx")
@Stateless
public class TestPropagation {

    @Path("/test")
    @Produces({"text/plain"})
    public String test() {
        return "test";
    }
}