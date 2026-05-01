public class Point {
    public double x, y;
    public int xIdx; // position in the globally x-sorted array, assigned once after sorting

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
