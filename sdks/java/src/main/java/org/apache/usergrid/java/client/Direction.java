package org.apache.usergrid.java.client;

public enum Direction {
    OUT,
    IN,
    BOTH;

    public static final Direction[] proper;

    static {
        proper = new Direction[]{OUT, IN};
    }

    private Direction() {
    }

    public Direction opposite() {
        return this.equals(OUT) ? IN : (this.equals(IN) ? OUT : BOTH);
    }
}