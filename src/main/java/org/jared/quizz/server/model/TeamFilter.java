package org.jared.quizz.server.model;

public class TeamFilter {

    private TeamFilterType type;
    private String desiratedValue;

    public TeamFilterType getType() {
        return type;
    }

    public TeamFilter setType(TeamFilterType type) {
        this.type = type;
        return this;
    }

    public String getDesiratedValue() {
        return desiratedValue;
    }

    public TeamFilter setDesiratedValue(String desiratedValue) {
        this.desiratedValue = desiratedValue;
        return this;
    }

}
