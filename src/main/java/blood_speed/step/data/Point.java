package blood_speed.step.data;

import java.util.Objects;

public class Point {
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = !Double.isNaN(x) ? x : 0;
        this.y = !Double.isNaN(y) ? y : 0;
    }

    private final double x;
    private final double y;

    public int getIntX() {
        return (int) Math.round(x);
    }

    public int getIntY() {
        return (int) Math.round(y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.getIntX(), getIntX()) == 0 &&
                Double.compare(point.getIntY(), getIntY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIntX(), getIntY());
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + getX() +
                ", y=" + getY() +
                '}';
    }
}
