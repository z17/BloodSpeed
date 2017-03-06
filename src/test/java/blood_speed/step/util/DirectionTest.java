package blood_speed.step.util;

import blood_speed.step.data.Point;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DirectionTest {
    @Test
    public void getByPointsTop() throws Exception {

        Point current = new Point(5, 5);

        Point next = new Point(5 , 9);

        assertThat(Direction.getByPoints(current, next), is(Direction.BOTTOM));
    }

    @Test
    public void getByPointsLeft() throws Exception {

        Point current = new Point(5, 5);

        Point next = new Point(1 , 5);

        assertThat(Direction.getByPoints(current, next), is(Direction.LEFT));
    }

    @Test
    public void getByPointsTopRight() throws Exception {

        Point current = new Point(5, 5);

        Point next = new Point(10, 11);

        assertThat(Direction.getByPoints(current, next), is(Direction.BOTTOM_RIGHT));
    }

    @Test
    public void getByPointBottomLeft() throws Exception {

        Point current = new Point(9, 0);

        Point next = new Point(4, 11);

        assertThat(Direction.getByPoints(current, next), is(Direction.BOTTOM_LEFT));
    }


    @Test
    public void getByPointBottomLeft2() throws Exception {

        Point current = new Point(33, 29);

        Point next = new Point(26  , 36);

        Direction byPoints = Direction.getByPoints(current, next);
        System.out.println(byPoints);
        assertThat(Direction.getByPoints(current, next), is(Direction.BOTTOM_LEFT));
    }
}