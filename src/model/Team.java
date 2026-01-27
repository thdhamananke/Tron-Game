package model;

import java.util.*;

public class Team {
    private String name;
    private List<Player> members;
    private Color color;

    public Team(String name, List<Player> members, Color color) {
        this.name = name;
        this.members = members;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public List<Player> getMembers() {
        return this.members;
    }
}
