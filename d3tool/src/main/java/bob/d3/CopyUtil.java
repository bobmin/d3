package bob.d3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class CopyUtil {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(CopyUtil.class.getName());

	public void copy(final File src, final File dst) throws IOException {

		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException ex) {
					LOG.warning("[out] not closable: " + ex.getMessage());
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException ex) {
					LOG.warning("[in] not closable: " + ex.getMessage());
				}
			}
		}

	}

}
