import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

public class FindConstantTerm {

    static class Point {
        BigDecimal x;
        BigDecimal y;

        Point(BigDecimal x, BigDecimal y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {

        FileReader reader = new FileReader("testcases.json");
        JSONObject json = new JSONObject(new JSONTokener(reader));

        Iterator<String> testcases = json.keys();

        while (testcases.hasNext()) {

            String testcaseName = testcases.next();
            JSONObject testcase = json.getJSONObject(testcaseName);
            JSONObject keys = testcase.getJSONObject("keys");

            int k = keys.getInt("k");

            List<Point> points = new ArrayList<>();

            // Step 1: Collect all x values
            List<Integer> xValues = new ArrayList<>();
            Iterator<String> pointKeys = testcase.keys();

            while (pointKeys.hasNext()) {
                String key = pointKeys.next();
                if (!key.equals("keys")) {
                    xValues.add(Integer.parseInt(key));
                }
            }

            // Step 2: Sort x values
            Collections.sort(xValues);

            // Step 3: Take first k points
            for (int i = 0; i < k; i++) {

                int xKey = xValues.get(i);
                JSONObject obj = testcase.getJSONObject(String.valueOf(xKey));

                int base = Integer.parseInt(obj.getString("base"));
                String valueStr = obj.getString("value");

                // Convert from base to decimal
                BigInteger yInt = new BigInteger(valueStr, base);

                BigDecimal x = new BigDecimal(xKey);
                BigDecimal y = new BigDecimal(yInt);

                points.add(new Point(x, y));
            }

            // Compute constant term
            BigDecimal c = lagrangeAtZero(points);

            System.out.println(testcaseName + " Constant term (c) = " + c.toBigInteger());
        }
    }

    public static BigDecimal lagrangeAtZero(List<Point> points) {

        MathContext mc = new MathContext(120);
        BigDecimal result = BigDecimal.ZERO;

        int k = points.size();

        for (int i = 0; i < k; i++) {

            BigDecimal xi = points.get(i).x;
            BigDecimal yi = points.get(i).y;

            BigDecimal term = yi;

            for (int j = 0; j < k; j++) {

                if (i == j) continue;

                BigDecimal xj = points.get(j).x;

                BigDecimal numerator = xj.negate(); 
                BigDecimal denominator = xi.subtract(xj);

                term = term.multiply(numerator, mc)
                           .divide(denominator, mc);
            }

            result = result.add(term, mc);
        }

        // Round to nearest integer
        return result.setScale(0, RoundingMode.HALF_UP);
    }
}
