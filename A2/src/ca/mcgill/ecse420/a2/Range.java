package ca.mcgill.ecse420.a2;

/**
 * Range encapsulates a given time interval [t0, t1]
 * as well as methods associated with comparing ranges
 */
public class Range {

  private long t0, t1;

  public Range(long t0, long t1) {
    this.t0 = t0;
    this.t1 = t1;
  }

  // determine if ranges a and b overlap
  public static boolean overlap(Range a, Range b) {
    return !(a.t0 < b.t0 && a.t1 < b.t1 || a.t0 > b.t0 && a.t0 > b.t1);
  }
}
