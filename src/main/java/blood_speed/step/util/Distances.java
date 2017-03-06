package blood_speed.step.util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Distances {
    final Pair<Direction, Double> top;
    final Pair<Direction, Double> bottom;
    final Pair<Direction, Double> right;
    final Pair<Direction, Double> left;

    final Pair<Direction, Double> topRight;
    final Pair<Direction, Double> topLeft;
    final Pair<Direction, Double> bottomRight;
    final Pair<Direction, Double> bottomLeft;

    public Distances(int top, int bottom, int right, int left, double topRight, double topLeft, double bottomRight, double bottomLeft) {
        this.top = new Pair<>(Direction.TOP, (double) top);
        this.bottom = new Pair<>(Direction.BOTTOM, (double) bottom);
        this.right = new Pair<>(Direction.RIGHT, (double) right);
        this.left = new Pair<>(Direction.LEFT, (double) left);
        this.topRight = new Pair<>(Direction.TOP_RIGHT, topRight);
        this.topLeft = new Pair<>(Direction.TOP_LEFT, topLeft);
        this.bottomRight = new Pair<>(Direction.BOTTOM_RIGHT, bottomRight);
        this.bottomLeft = new Pair<>(Direction.BOTTOM_LEFT, bottomLeft);
    }

    public Double getMinDirectionValue() {
        List<Pair<Direction, Double>> result = new ArrayList<>();
        result.add(top);
        result.add(right);
        result.add(left);
        result.add(bottom);
        result.add(bottomRight);
        result.add(bottomLeft);
        result.add(topLeft);
        result.add(topRight);
        result.sort(Comparator.comparing(Pair::getValue));
        return result.get(0).getValue();
    }

    public Double getTop() {
        return top.getValue();
    }

    public Double getBottom() {
        return bottom.getValue();
    }

    public Double getRight() {
        return right.getValue();
    }

    public Double getLeft() {
        return left.getValue();
    }

    public Double getTopRight() {
        return topRight.getValue();
    }

    public Double getTopLeft() {
        return topLeft.getValue();
    }

    public Double getBottomRight() {
        return bottomRight.getValue();
    }

    public Double getBottomLeft() {
        return bottomLeft.getValue();
    }
}
