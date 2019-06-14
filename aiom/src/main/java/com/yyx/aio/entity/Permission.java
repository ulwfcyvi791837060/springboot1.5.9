package com.yyx.aio.entity;

import lombok.Data;

@Data
public class Permission {

    private Integer id;

    private String name;

    private String permissionUrl;

    private String method;

    private String description;
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name=" + name +
                ", permissionUrl=" + permissionUrl +
                ", method=" + method +
                ", description=" + description +
                '}';
    }
}