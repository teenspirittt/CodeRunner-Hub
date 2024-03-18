package com.teenspirit.coderunnerhub.containermanager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Container {

    private final String id;
    private final String imageName;
    @Getter
    private boolean available;

    public Container(String id, String imageName) {
        this.id = id;
        this.imageName = imageName;

    }
}
