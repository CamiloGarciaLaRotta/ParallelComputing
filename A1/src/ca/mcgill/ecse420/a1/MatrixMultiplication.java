package ca.mcgill.ecse420.a1;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

public class MatrixMultiplication {

	private static int NUM_OF_THREADS = 1;
	private static final int MATRIX_SIZE = 2000;
	// Unecessary to go above MAX_THREADS
	// it is the mathematical exact amount of dot products we will perform
	private static final int MAX_THREADS = 801;

	public static void main(String[] args) {
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		System.out.println("Sequential Multiplication");
		timer(MatrixMultiplication::sequentialMultiplyMatrix, a, b);

		System.out.println("Parallel Multiplication");
		System.out.println("# Threads\t\tExecution Time");
		for (int i=1; i<MAX_THREADS; i+= 10) {
			System.out.print(i + " thread[s]\t");
			NUM_OF_THREADS = i;
			timer(MatrixMultiplication::parallelMultiplyMatrix, a, b);
		}

		System.exit(0);
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
		final int COLS_B = b[0].length;
		final int INNER_DIM = a[0].length;
		double[][] C = new double[ROWS_A][COLS_B];

		for (int r=0; r<ROWS_A; r++) {
			for (int c=0; c<COLS_B; c++) {
				for (int k=0; k<INNER_DIM; k++) {
					C[r][c] += a[r][k] * b[k][c];
				}
			}
		}

		return C;
	}

	/**
	 * Returns the result of a concurrent matrix multiplication The two matrices are
	 * randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		if (a[1].length != b.length) {
			throw new InputMismatchException("Invalid matrix sizes to perform multiplication.");
		}

		final int ROWS_A = a.length;
		final int COLS_B = b[0].length;
		final int INNER_DIM = a[0].length;
		final double[][] C = new double[ROWS_A][COLS_B];

		// instantiate an ExecutorService and a list of tasks to execute
		final int numOfTasks = ROWS_A * COLS_B;
		ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
		ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(numOfTasks);

		for (int r=0; r<ROWS_A; r++) {
			for (int c=0; c<COLS_B; c++) {
				// add every dot product task to the list of tasks to execute
				tasks.add(Executors.callable(new DotProductTask(a, b, C, r, c, INNER_DIM)));
			}
		}

		try {
			// execute and wait for all tasks to complete
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return C;
	}

	/**
	 * Calculates the dot product of 2 vectors
	 * then writes the result in the correspondent row/col of an output matrix
	 */
	private static class DotProductTask implements Runnable {
		double[][] a, b, C;
		int row, col, dim;
		private Lock lock = new ReentrantLock();

		public DotProductTask(double[][] a, double[][] b, double[][] C, final int row,final int col, final int dim) {
			this.a = a;
			this.b = b;
			this.C = C;
			this.row = row;
			this.col = col;
			this.dim = dim;
		}

		public void run() {
			double result = 0;
			// perform the dot product
			for (int k=0; k<dim; k++) {
				result += a[row][k] * b[k][col];
			}

			// update the cell with the result of the dot product
			// this is the only operation that requires mutual exclusivity
			lock.lock();
			try {
				C[row][col] = result;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			  lock.unlock();
			}
		}
	}

	/**
	 * Wraps around matrix multiplication functions to time its execution
	 * This function solves A1 1.3
	 * @param matrixMultiplier the function that multiplies 2 matrices axb
	 * @param a the first matrix
	 * @param b the second matrix
	 * @return the outcome of axb. The execution time is sent to stdout
	 */
	public static double[][] timer(BiFunction<double[][], double[][], double[][]> matrixMultiplier, double[][] a, double[][] b) {
		long startTime = System.nanoTime();
		double[][] C = matrixMultiplier.apply(a, b);
		long stopTime = System.nanoTime();

        System.out.print((stopTime-startTime) + "ns\n");

		return C;
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
