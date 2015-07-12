package net.slisenko.jpa.examples.inheritance.singleTable;

import net.slisenko.Identity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Discriminator column can be also String
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
public class SingleTableBase extends Identity {

}
