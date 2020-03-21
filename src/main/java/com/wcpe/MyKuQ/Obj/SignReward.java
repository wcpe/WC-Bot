package com.wcpe.MyKuQ.Obj;

import java.util.List;

public class SignReward {
    public SignReward(String name, int point, List<String> commands) {
        this.name = name;
        this.point = point;
        this.commands = commands;
    }

    private String name;
    private int point;
    private List<String> commands;

    public String getName() {
        return name;
    }

    public int getPoint() {
        return point;
    }

    public List<String> getCommands() {
        return commands;
    }
}
