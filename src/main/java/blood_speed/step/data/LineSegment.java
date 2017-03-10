package blood_speed.step.data;

import blood_speed.helper.MathHelper;

import java.util.Objects;

/**
 * Line aX + bY + c = 0
 */
public class LineSegment {
    private final Point p1;
    private final Point p2;
    private final double a;
    private final double b;
    private final double c;

    public LineSegment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;

        double a = p2.getY() - p1.getY();
        double b = p1.getX() - p2.getX();
        double c = -(p1.getX() * p2.getY() - p2.getX() * p1.getY());

        if (a < 0) {
            a = - a;
            b = -b;
            c = -c;
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineSegment that = (LineSegment) o;
        return Double.compare(that.getA(), getA()) == 0 &&
                Double.compare(that.getB(), getB()) == 0 &&
                Double.compare(that.getC(), getC()) == 0 &&
                (Objects.equals(getP1(), that.getP1()) && Objects.equals(getP2(), that.getP2())
                        || Objects.equals(getP1(), that.getP2()) && Objects.equals(getP2(), that.getP1()))
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getP1(), getP2(), getA(), getB(), getC());
    }

    @Override
    public String toString() {
        return "LineSegment{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", line: " +
                a + "*x + " +
                b + "*y + " +
                c + " = 0 }";
    }

    public boolean isPointOnSegment(final Point p) {
        double v = a * p.getX() + b * p.getY() + c;
        if (Math.abs(v) < MathHelper.EPSILON) {
            return (p1.getX() <= p.getX() &&  p.getX() <= p2.getX() || p1.getX() >= p.getX() && p.getX() >= p2.getX())
                    && (p1.getY() <= p.getY() &&  p.getY() <= p2.getY() || p1.getY() >= p.getY() && p.getY() >= p2.getY());
        }
        return false;
    }
}
