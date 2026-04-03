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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); 
    }
}
