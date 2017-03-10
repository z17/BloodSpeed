package blood_speed.helper;

import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MathHelperTest {
    @Test
    public void getInterSectionPointWithCircleAndSegment() throws Exception {


        Point p1 = new Point(2,4);
        Point p2 = new Point(1,7);
        LineSegment segment = new LineSegment(p1, p2);

        Point center = new Point(2,2);
        double r = 1;

        Point inter1 = MathHelper.getInterSectionPointWithCircleAndSegment(segment, center, r);

        assertThat(inter1, is(nullValue()));

        Point p3 = new Point(3,1);
        LineSegment segment2 = new LineSegment(p3, p2);
        Point inter2 = MathHelper.getInterSectionPointWithCircleAndSegment(segment2, center, r);
        System.out.println(inter2);
    }

}