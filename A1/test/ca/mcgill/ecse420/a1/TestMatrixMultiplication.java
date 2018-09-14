package ca.mcgill.ecse420.a1;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class TestMatrixMultiplication {

    public class TestCase {
        public double[][] a;
        public double[][] b;
        public double[][] expected;

        public TestCase(double[][] a, double[][] b, double[][] expected) {
            this.a = a;
            this.b = b;
            this.expected = expected;
        }

        public TestCase() {}
    }



    @Test
    public void testMatrixMultiplication() {
        TestCase[] testCases = new TestCase[3];
        testCases[0] = new TestCase(
            new double[][]{
                { 1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
            },
            new double[][]{
                { 5, 5, 5 },
                { 4, 4, 4 },
                { 3, 3, 3 }
            },
            new double[][]{
                { 5, 5, 5 },
                { 4, 4, 4 },
                { 3, 3, 3 }
            }
        );
        testCases[1] = new TestCase(
            new double[][]{
                { 5, 5, 5 },
                { 4, 4, 4 },
                { 3, 3, 3 }
            },
            new double[][]{
                { 1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
            },
            new double[][]{
                { 5, 5, 5 },
                { 4, 4, 4 },
                { 3, 3, 3 }
            }
        );
        testCases[2] = new TestCase(
            new double[][]{
                { 1, 2, 3 },
                { 4, 5, 6 },
            },
            new double[][]{
                { 7, 8 },
                { 9, 10 },
                { 11, 12 }
            },
            new double[][]{
                { 58, 64 },
                { 139, 154}
            }
        );

        System.out.println("\nSEQUENTIAL");
        for (TestCase tc : testCases) {
            assertArrayEquals(tc.expected, MatrixMultiplication.timer(
                MatrixMultiplication::sequentialMultiplyMatrix, tc.a, tc.b)
                );
            }

        System.out.println("\nPARALLEL");
        for (TestCase tc : testCases) {
            assertArrayEquals(tc.expected, MatrixMultiplication.timer(
                MatrixMultiplication::parallelMultiplyMatrix, tc.a, tc.b)
            );
        }
    }
}
