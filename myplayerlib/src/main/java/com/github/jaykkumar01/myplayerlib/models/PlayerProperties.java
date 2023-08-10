package com.github.jaykkumar01.myplayerlib.models;

import java.io.Serializable;

public class PlayerProperties implements Serializable {
    String path;
    String title;
    String subPath;

    public PlayerProperties(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }
}
