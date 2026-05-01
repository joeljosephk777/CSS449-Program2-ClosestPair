import java.io.*;
import java.util.*;

public class PointSet {
    private int n;
    private Point[] pointsByX; // globally x-sorted; indices are the canonical names printed in output

    public PointSet(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(filename));
        n = scanner.nextInt();
        pointsByX = new Point[n];
        for (int i = 0; i < n; i++) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            pointsByX[i] = new Point(x, y);
        }
        scanner.close();

        Arrays.sort(pointsByX, (a, b) -> Double.compare(a.x, b.x));
        for (int i = 0; i < n; i++)
            pointsByX[i].xIdx = i;
    }

    public void solve() {
        if (n < 2) return;
        Point[] pointsByY = pointsByX.clone();
        Arrays.sort(pointsByY, (a, b) -> Double.compare(a.y, b.y));
        closestPair(0, n - 1, pointsByY);
    }

    // Returns the closest-pair distance for pointsByX[left..right].
    // py is the same point set sorted by y (maintained across calls for O(n log n)).
    // Prints each subproblem result in post-order (left, right, current).
    private double closestPair(int left, int right, Point[] py) {
        int count = right - left + 1;

        // Base case: brute-force for 3 or fewer points
        if (count <= 3) {
            double min = Double.MAX_VALUE;
            for (int i = left; i <= right; i++)
                for (int j = i + 1; j <= right; j++) {
                    double d = pointsByX[i].distTo(pointsByX[j]);
                    if (d < min) min = d;
                }
            System.out.printf("D[%d,%d]: %.4f%n", left, right, min);
            return min;
        }

        // Integer division gives left group one extra point when count is odd
        int mid = (left + right) / 2;
        double midX = pointsByX[mid].x;

        // Partition py into left/right halves while preserving y-sort order — O(n)
        Point[] leftPY  = new Point[mid - left + 1];
        Point[] rightPY = new Point[right - mid];
        int li = 0, ri = 0;
        for (Point p : py) {
            if (p.xIdx <= mid) leftPY[li++] = p;
            else               rightPY[ri++] = p;
        }

        double dLeft  = closestPair(left,    mid,   leftPY);
        double dRight = closestPair(mid + 1, right, rightPY);
        double d = Math.min(dLeft, dRight);

        // Strip: points within d of the dividing line, already y-sorted because py is
        List<Point> strip = new ArrayList<>();
        for (Point p : py)
            if (Math.abs(p.x - midX) < d) strip.add(p);

        // At most 8 neighbours per point need checking in the strip
        for (int i = 0; i < strip.size(); i++) {
            Point pi = strip.get(i);
            for (int j = i + 1; j < strip.size(); j++) {
                Point pj = strip.get(j);
                if (pj.y - pi.y >= d) break; // y gap too large, all remaining are farther
                double dist = pi.distTo(pj);
                if (dist < d) d = dist;
            }
        }

        System.out.printf("D[%d,%d]: %.4f%n", left, right, d);
        return d;
    }
}
