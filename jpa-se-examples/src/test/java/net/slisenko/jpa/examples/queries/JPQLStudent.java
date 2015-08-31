package net.slisenko.jpa.examples.queries;

import net.slisenko.Identity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class JPQLStudent extends Identity {

    @OneToMany(mappedBy = "student")
    private List<JPQLPresentation> presentations = new ArrayList<>();

    public List<JPQLPresentation> getPresentations() {
        return presentations;
    }

    public void setPresentations(List<JPQLPresentation> presentations) {
        this.presentations = presentations;
    }
}
