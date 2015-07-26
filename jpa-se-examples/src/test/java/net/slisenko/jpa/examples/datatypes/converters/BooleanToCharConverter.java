package net.slisenko.jpa.examples.datatypes.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToCharConverter implements AttributeConverter<Boolean, Character> {

    @Override
    public Character convertToDatabaseColumn(Boolean aBoolean) {
        return aBoolean ? 'Y' : 'N';
    }

    @Override
    public Boolean convertToEntityAttribute(Character character) {
        if (character == 'Y') {
            return true;
        } else {
            return false;
        }
    }
}