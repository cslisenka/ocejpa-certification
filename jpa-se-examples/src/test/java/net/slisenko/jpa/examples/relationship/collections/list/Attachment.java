package net.slisenko.jpa.examples.relationship.collections.list;

import javax.persistence.Embeddable;

@Embeddable
public class Attachment {

    private String filename;
    private int filesize;

    public Attachment(String filename, int filesize) {
        this.filename = filename;
        this.filesize = filesize;
    }

    public Attachment() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "filename='" + filename + '\'' +
                ", filesize=" + filesize +
                '}';
    }
}