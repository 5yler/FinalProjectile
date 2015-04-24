import java.lang.IllegalArgumentException;

public class Control
{
  private double _s;
  private double _omega;
  
  public Control (double s, double omega){
    if (s < 5 || s > 10) //Check to make sure s is in range.
      throw new IllegalArgumentException("S out of range");
    if (omega < -Math.PI || omega >= Math.PI) //Check to make sure theta is in range.
      throw new IllegalArgumentException("Omega out of range");
    
    _s = s;
    _omega = omega;    
  }
  
  public double getSpeed() {
    return _s;
  }

  public double getRotVel() {
    return _omega;
  }  
}
