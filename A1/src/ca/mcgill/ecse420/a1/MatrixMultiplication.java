package ca.mcgill.ecse420.a1;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

	private static final int NUMBER_THREADS = 1;
	private static final int MATRIX_SIZE = 2000;

	public static void main(String[] args) {

		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		sequentialMultiplyMatrix(a, b);
		parallelMultiplyMatrix(a, b);
	}

	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		if (a[1].length != b.length) {
			throw new InputMismatchException("Invalid matrix sizes to perform multiplication.");
		}

		final int ROWS_A = a.length;
		final int COLS_A = a[0].length;
		final int COLS_B = b[0].length;
		double[][] C = new double[ROWS_A][COLS_B];

		for (int r=0; r<ROWS_A; r++) {
			for (int c=0; c<COLS_B; c++) {
				for (int k=0; k<COLS_A; k++) {
					C[r][c] += a[r][k] * b[k][c];
				}
			}
		}

		return C;
	}

	/**
	 * Returns the result of a concurrent matrix multiplication The two matrices are
	 * randomly generated
	 *
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		if (a[1].length != b.length) {
			throw new InputMismatchException("Invalid matrix sizes to perform multiplication.");
		}

		final int ROWS_A = a.length;
		final int COLS_A = a[0].length;
		final int COLS_B = b[0].length;
		final double[][] C = new double[ROWS_A][COLS_B];

	// 	ExecutorService executor = Executors.newCachedThreadPool();

	// 	for (int r=0; r<ROWS_A; r++) {
	// 		for (int c=0; c<COLS_B; c++) {
	// 			executor.execute(new Runnable() {

	// 				public void run() {
	// 					for (int k=0; k<COLS_A; k++) {
	// 						C[r][c] += a[r][k] * b[k][c];
	// 					}
	// 				}
	// 			});
	// 		}
	// 	}

	// 	executor.shutdown();
	// 	while (executor.isTerminated() == false) {
	// 		try {
	// 			Thread.sleep(50);
	// 		} catch (InterruptedException e) {
	// 			e.printStackTrace();
	// 		}
	// 	}

		return C;
	}

	/**
	 * Prints the 2D matrix in grid form
	 * @param matrix the 2D matrix to print
	 */
	public static void printMatrix(double[][] matrix) {
		for(int r=0; r<matrix.length; r++) {
			for(int c=0; c<matrix[r].length; c++)
				System.out.print(matrix[r][c] + " ");
			System.out.println();
		}
	}

	/**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix (int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }

}
