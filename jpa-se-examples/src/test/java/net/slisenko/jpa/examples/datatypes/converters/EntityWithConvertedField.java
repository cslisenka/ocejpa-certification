package net.slisenko.jpa.examples.datatypes.converters;

import net.slisenko.Identity;

import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EntityWithConvertedField extends Identity {

    @Convert(converter = BooleanToCharConverter.class)
    boolean converterValue;

    @ElementCollection
    @Convert(converter = BooleanToCharConverter.class)
    private List<Boolean> convertedValueCollection = new ArrayList<>();

    public boolean isConverterValue() {
        return converterValue;
    }

    public void setConverterValue(boolean converterValue) {
        this.converterValue = converterValue;
    }

    public List<Boolean> getConvertedValueCollection() {
        return convertedValueCollection;
    }

    public void setConvertedValueCollection(List<Boolean> convertedValueCollection) {
        this.convertedValueCollection = convertedValueCollection;
    }

    @Override
    public String toString() {
        return "EntityWithConvertedField{" +
                "converterValue=" + converterValue +
                ", convertedValueCollection=" + convertedValueCollection +
                '}';
    }
}