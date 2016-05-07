package com.traveltogether.model.common;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable{

    private Long groupId;
    private String groupName;
    private List<User> users;

    public Group() {}

}
