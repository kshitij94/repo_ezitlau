import javax.swing.JProgressBar;

public class ProgressBarListener
{


  private JProgressBar progressBar = null;
  private long contentLength;
  public ProgressBarListener()
  {
    super();
  }

  public ProgressBarListener(JProgressBar progressBar, long contentLong)
  {
    this();
    this.progressBar = progressBar;
    this.contentLength = contentLong;
  }

  public void updateTransferred(long transferedBytes)
  {
   
    this.progressBar.setValue((int) (100 * transferedBytes/this.contentLength));
    this.progressBar.paint(progressBar.getGraphics());
    //System.out.println("Transferred: " + transferedBytes + " bytes.");
  }
}