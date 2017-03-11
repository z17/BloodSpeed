package blood_speed.step.data;

public class Line {
    final double a;
    final double b;
    final double c;

    public Line(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Line(Point p1, Point p2) {
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

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    /**
     * x = (-bY - c) / a
     */
    public double getX(final double y) {
        return (-b * y - c) / a;
    }

    /**
     *  y = (-aX - c) / b
     */
    public double getY(final double x) {
        return (-a * x - c) / b;
    }

}
