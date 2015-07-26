package net.slisenko.jpa.examples.primarykey.shared;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
public class EmbeddedIdClass implements Serializable {

    @OneToOne
    private MainEntity main;

    private String ownName;

    public MainEntity getMain() {
        return main;
    }

    public void setMain(MainEntity main) {
        this.main = main;
    }

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmbeddedIdClass that = (EmbeddedIdClass) o;

        if (main != null ? !main.equals(that.main) : that.main != null) return false;
        return !(ownName != null ? !ownName.equals(that.ownName) : that.ownName != null);
    }

    @Override
    public int hashCode() {
        int result = main != null ? main.hashCode() : 0;
        result = 31 * result + (ownName != null ? ownName.hashCode() : 0);
        return result;
    }
}
