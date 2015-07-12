package net.slisenko.jpa.examples.relationship.collections.list;

import net.slisenko.Identity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Task extends Identity {

    // By default collections are loaded LAZY (General rule)
    // If we want everything loaded in 1 SQL, we need to specify EAGER
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "task_comment")
    private List<String> comments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_custom_attachments", joinColumns = @JoinColumn(name = "task_id"))
    @AttributeOverride(name = "filename", column = @Column(name = "fileRenamed"))
    private List<Attachment> attachments = new ArrayList<>();

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", comments=" + comments +
                ", attachments=" + attachments +
                "} " + super.toString();
    }
}