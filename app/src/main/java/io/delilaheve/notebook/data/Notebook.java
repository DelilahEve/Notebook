package io.delilaheve.notebook.data;

import java.io.Serializable;

import io.delilaheve.notebook.common.Common;

public class Notebook implements Serializable {

    private int id = -1;
    private int colour;

    private String header;
    private String password;

    public Notebook(String header, int colour) {
        this.header = header;
        this.colour = colour;
    }

    public Notebook(String header, String password, int colour) {
        this.header = header;
        this.password = password;
        this.colour = colour;
    }

    public Notebook(int id, String header, String password, int colour) {
        this.header = header;
        this.password = password;
        this.colour = colour;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Notebook))
            return false;

        return getId() == ((Notebook) o).getId();
    }

    public void save() {
        if(id == -1)
            id = Common.helper.addNotebook(this);
        else
            Common.helper.updateNotebook(this);
    }
}
