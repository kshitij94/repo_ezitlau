import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.AbstractHttpEntity;


public class FileEntityWithProgressBar extends AbstractHttpEntity implements Cloneable
{

  protected final File file;
  private final ProgressBarListener listener;
  private long transferredBytes;
  private int bufferSize;
  public FileEntityWithProgressBar(final File file, final String contentType, ProgressBarListener listener,int bufferSize)
  {
    super();
    if (file == null)
    {
      throw new IllegalArgumentException("File may not be null");
    }
    this.file = file;
    this.listener = listener;
    this.bufferSize = bufferSize;
    this.transferredBytes = 0;
    setContentType(contentType);
  }

  public boolean isRepeatable()
  {
    return true;
  }

  public long getContentLength()
  {
    return this.file.length();
  }

  public InputStream getContent() throws IOException
  {
    return new FileInputStream(this.file);
  }

  public void writeTo(final OutputStream outstream) throws IOException
  {
    if (outstream == null)
    {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    InputStream instream = new FileInputStream(this.file);
    try
    {
      byte[] tmp = new byte[this.bufferSize];
      int l;
      while ((l = instream.read(tmp)) != -1)
      {
        outstream.write(tmp, 0, l);
        this.transferredBytes += l;
        this.listener.updateTransferred(this.transferredBytes);
      }
      outstream.flush();
    }
    finally
    {
      instream.close();
    }
  }

  public boolean isStreaming()
  {
    return false;
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}