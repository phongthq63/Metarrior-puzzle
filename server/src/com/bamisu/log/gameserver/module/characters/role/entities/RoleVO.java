package com.bamisu.log.gameserver.module.characters.role.entities;

public class RoleVO {
    public String id;
    public String name;
    public String description;

    public static RoleVO createRole(String id, String name, String description) {
        RoleVO role = new RoleVO();
        role.id = id;
        role.name = name;
        role.description = description;

        return role;
    }
}
