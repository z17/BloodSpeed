package blood_speed.step.data;

import blood_speed.helper.MathHelper;

import java.util.Objects;

/**
 * Line aX + bY + c = 0
 */
public class LineSegment extends Line {
    private final Point p1;
    private final Point p2;

    public LineSegment(Point p1, Point p2) {
        super(p1, p2);
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
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
        if (MathHelper.doubleEquals(v, 0)) {
            return (p1.getX() < p.getX() &&  p.getX() < p2.getX() || p1.getX() > p.getX() && p.getX() > p2.getX() || MathHelper.doubleEquals(p.getX(), p1.getX()) || MathHelper.doubleEquals(p.getX(), p2.getX()))
                    && (p1.getY() < p.getY() &&  p.getY() < p2.getY() || p1.getY() > p.getY() && p.getY() > p2.getY() || MathHelper.doubleEquals(p.getY(), p1.getY()) || MathHelper.doubleEquals(p.getY(), p2.getY()));
        }
        return false;
    }

}
