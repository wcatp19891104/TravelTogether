package com.traveltogether.model.common;

import java.io.Serializable;

public class User implements Serializable{

    private Long userId;
    private Long groupId;
    private String userName;

    public User(Long userId, Long groupId, String userName) {
        this.userId = userId;
        this.groupId = groupId;
        this.userName = userName;
    }

    public User() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!getUserId().equals(user.getUserId())) return false;
        if (!getGroupId().equals(user.getGroupId())) return false;
        return getUserName().equals(user.getUserName());

    }

    @Override
    public int hashCode() {
        int result = getUserId().hashCode();
        result = 31 * result + getGroupId().hashCode();
        result = 31 * result + getUserName().hashCode();
        return result;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public String getUserName() {
        return this.userName;
    }

}
