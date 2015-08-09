package net.slisenko.jpa.examples.ee.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/session")
@Stateful
public class SessionbeanWithErrors {

    private List<Integer> numbers = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct");
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public int addNumber() {
        numbers.add((int)(Math.random() * 1000));
        System.out.println(this);
        return numbers.size();
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Integer> getNumbers() {
        return numbers;
    }

    @GET
    @Path("/throw")
    @Produces(MediaType.APPLICATION_JSON)
    public void throwException() {
        throw new RuntimeException("some error");
    }

    @Remove
    public void die() {
        System.out.println("Session bean removed!!!!!!!");
    }
}
