package blood_speed.step.data;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class LineSegmentTest {

    @Test
    public void isPointOnSegment() throws Exception {

        Point p1 = new Point(2, 2);
        Point p2 = new Point(5, 5);
        LineSegment ls1 = new LineSegment(p1, p2);

        Point pt1 = new Point(3, 3);
        Point pt2 = new Point(7, 7);

        Assert.assertThat(ls1.isPointOnSegment(pt1), is(true));
        Assert.assertThat(ls1.isPointOnSegment(pt2), is(false));
    }

    @Test
    public void equalsTest() throws Exception {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(13, 22);

        LineSegment ls1 = new LineSegment(p1, p2);
        LineSegment ls2 = new LineSegment(p2, p1);

        Assert.assertThat(ls1, equalTo(ls2));
    }
}