
package com.nascenia.albarakahhajj.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {

    private ArrayList<User> mUserList = new ArrayList<>();

    public ArrayList<User> getUserList() {
        return mUserList;
    }

    public void setUserList(ArrayList<User> userList) {
        mUserList = userList;
    }

    public void addUser(User user){
        mUserList.add(user);
    }
}
