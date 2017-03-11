package blood_speed.helper;

import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;
import org.junit.Test;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MathHelperTest {
    @Test
    public void getInterSectionPointWithCircleAndSegmentNull() throws Exception {
        Point p1 = new Point(2,4);
        Point p2 = new Point(1,7);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(2,2);
        double r = 1;

        Point inter1 = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);

        assertThat(inter1, is(nullValue()));
    }

    @Test
    public void getInterSectionPointWithCircleAndSegmentException() throws Exception {
        Point p2 = new Point(1,7);
        Point p3 = new Point(3,1);

        Point center = new Point(2,2);
        double r = 1;

        LineSegment segment2 = new LineSegment(p3, p2);
        boolean fl = false;
        try {
            Point inter2 = MathHelper.getInterSectionPointWithCircleAndSegment(segment2, center, r);

        } catch (RuntimeException e) {
            fl = true;
        }

        assertThat(fl, is(true));
    }

    @Test
    public void getInterSectionPointWithCircleAndSegmentValue() throws Exception {
        Point p1 = new Point(5,2);
        Point p2 = new Point(3,6);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(4,6);
        double r = 2;

        Point inter = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);

        assertThat(inter, is(new Point(4, 4)));
    }

    @Test
    public void getInterSectionPointWithCircleAnLineValue() throws Exception {
        Point p1 = new Point(44,109);
        Point p2 = new Point(44,108);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(44,111);
        double r = 3;

        Point point = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);
        assertThat(point, is(notNullValue()));
    }

    @Test
    public void getInterSectionPointWithCircleAnLineValue2() throws Exception {
        Point p1 = new Point(34,24);
        Point p2 = new Point(34,23);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(36.1161,25.116);
        double r = 3;

        Point point = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);
        System.out.println(point);
        assertThat(point, is(notNullValue()));
    }

    @Test
    public void getInterSectionPointWithCircleAnLineValue3() throws Exception {
        Point p1 = new Point(34,23);
        Point p2 = new Point(33,23);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(36.11617445270761,25.11617445270763);
        double r = 3;

        Point point = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);
        System.out.println(point);
        assertThat(point, is(notNullValue()));
    }
}