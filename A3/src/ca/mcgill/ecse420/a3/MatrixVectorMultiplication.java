package ca.mcgill.ecse420.a3;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

public class MatrixVectorMultiplication {

  private static int MATRIX_SIZE = 2000;
  private static int NUM_OF_THREADS = 25;

  public static void main(String[] args) {
    // Generate two random matrices, same size
    double[][] M = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    double[] V = generateRandomVector(MATRIX_SIZE);

    System.out.println("Sequential Multiplication");
    System.out.println("Execution Time (ns)");
    timer(MatrixVectorMultiplication::sequentialMultiplyMatrix, M, V);

    System.out.println("Parallel Multiplication ("+NUM_OF_THREADS+" threads)");
    System.out.println("Execution Time (ns)");
    timer(MatrixVectorMultiplication::parallelMultiplyMatrix, M, V);

    System.exit(0);
  }

  /**
  * Returns the result of a sequential matrix-vector multiplication
  * @param M is the matrix
  * @param V is the vector
  * @return the resulting vector
  * */
  public static double[] sequentialMultiplyMatrix(double[][] M, double[] V) {

    if (M[0].length != V.length) {
      throw new InputMismatchException("Invalid matrix sizes to perform multiplication.");
    }

    final int NUM_ROWS = M.length;
    final int NUM_COLS = M[0].length;

    double[] result = new double[NUM_COLS];

    for (int r=0; r<NUM_ROWS; r++) {
      for (int c=0; c<NUM_COLS; c++) {
        result[r] += M[r][c] * V[c];
      }
    }

    return result;
  }

  /**
  * Returns the result of a concurrent matrix-vector multiplication
  * @param M is the matrix
  * @param V is the vector
  * @return the resulting vector
  */
  public static double[] parallelMultiplyMatrix(double[][] M, double[] V) {

    if (M[0].length != V.length) {
      throw new InputMismatchException("Invalid matrix sizes to perform multiplication.");
    }

    final int NUM_ROWS = M.length;
    final int NUM_COLS = M[0].length;

    double[] result = new double[NUM_COLS];

    // instantiate an ExecutorService and M list of tasks to execute
    ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(NUM_OF_THREADS);
    final int increment = NUM_ROWS/NUM_OF_THREADS;

    for (int r=0; r<NUM_ROWS; r+=increment) {
      // add every dot product task to the list of tasks to execute
      tasks.add(Executors.callable(new DotProductTask(M, V, result, r, NUM_COLS, increment)));
    }

    try {
      // execute and wait for all tasks to complete
      executor.invokeAll(tasks);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
  * Calculates the dot product of 2 vectors
  * then writes the result in the correspondent row/col of an output matrix
  */
  private static class DotProductTask implements Runnable {
    double[][] M;
    double[] V, result;
    int row, dim, increment;
    private Lock lock = new ReentrantLock();

    public DotProductTask(double[][] M, double[] V, double[] result, final int row, final int dim, final int increment) {
      this.M = M;
      this.V = V;
      this.result = result;
      this.row = row;
      this.dim = dim;
      this.increment = increment;
    }

    public void run() {
      double[] result = new double[increment];
      // perform the dot product
      for (int r=this.row;r<this.row+increment; r++){
        for (int c=0; c<this.dim; c++) {
          result[r] += M[r][c] * V[c];
        }
      }

      // update the cell with the result of the dot product
      // this is the only operation that requires mutual exclusivity
      lock.lock();
      try {
        for (int r=this.row;r<this.row+increment;r++){
          this.result[r] = result[r-this.row];
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
      }
    }
  }

  /**
  * Wraps around matrix multiplication functions to time its execution in nanoseconds
  * @param matrixMultiplier the function that performs matrix-vector multiplication
  * @param M the matrix
  * @param V the vector
  * @return the resulting vector. The execution time is sent to stdout
  */
  public static double[] timer(BiFunction<double[][], double[], double[]> matrixMultiplier, double[][] M, double[] V) {
    long startTime = System.nanoTime();
    double[] result = matrixMultiplier.apply(M, V);
    long stopTime = System.nanoTime();

    System.out.println(stopTime-startTime);

    return result;
  }

  /**
  * Populates a matrix of given size with randomly generated integers between 0-10.
  * @param numRows number of rows
  * @param numCols number of cols
  * @return matrix
  */
  private static double[][] generateRandomMatrix (int numRows, int numCols) {
    double matrix[][] = new double[numRows][numCols];
    for (int row=0; row<numRows; row++) {
      for (int col=0; col<numCols; col++) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
    return matrix;
  }

  /**
  * Populates a vector of given size with randomly generated integers between 0-10.
  * @param size size of the vector
  * @return the vector
  */
  private static double[] generateRandomVector (int size) {
    double vector[] = new double[size];
    for (int i=0; i<size; i++) {
      vector[i] = (double) ((int) (Math.random() * 10.0));
    }
    return vector;
  }
}
