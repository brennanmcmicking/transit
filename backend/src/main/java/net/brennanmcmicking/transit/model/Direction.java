package net.brennanmcmicking.transit.model;

public enum Direction {
    UP,
    DOWN;

    @Override
    public String toString() {
        return switch (this) {
            case UP -> "UP";
            case DOWN -> "DOWN";
            default -> throw new IllegalStateException("Found direction other than UP or DOWN: " + this);
        };
    }
}
