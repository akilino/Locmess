package pt.ulisboa.tecnico.cmov.rest;

/**
 * Created by Sheng on 04/04/2017.
 */

public class Users {
    private String name;
    private String username;
    private int age;
    public Users(String name, String username, int age){
        this.name=name;
        this.username=username;
        this.age=age;
    }
    public String getName(){
        return name;
    }
    public String getUsername(){
        return username;
    }
    public int getAge(){
        return age;
    }
}
