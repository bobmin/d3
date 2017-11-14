package bob.d3.d3ext;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import bob.d3.d3ext.D3ExException.SourceException;

public class D3ExSourceFolder {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExSourceFolder.class.getName());

	private static final String SOURCE_PATH_KEY = "D3ExSourceFolder.source_path";

	private final File root;

	private List<File> bearbeitFolder = null;

	private final boolean development;

	private D3ExSourceFolder(File root) {
		Objects.requireNonNull(root);
		this.root = root;
		development = D3ExConfig.getDefault().isDevelomentActiv();
	}

	public static D3ExSourceFolder create() throws SourceException {
		D3ExConfig cfg = D3ExConfig.getDefault();
		String path = cfg.getProperty(SOURCE_PATH_KEY);
		if (null == path) {
			throw new D3ExException.SourceException("[path] is unknown in configuration, key = " + SOURCE_PATH_KEY,
					null);
		}
		File f = new File(path);
		if (!f.exists()) {
			throw new D3ExException.SourceException("[path] does not exist: " + path, null);
		}
		D3ExSourceFolder x = new D3ExSourceFolder(f);
		return x;
	}

	public File lookFor(String id, String folder, String erw) throws SourceException {
		Objects.requireNonNull(id);

		File f = null;

		File fa = new File(root, "public");
		File fa1 = new File(fa, folder);
		File fa2 = new File(fa1, id);
		if (fa2.exists()) {
			f = fa2;

		} else {
			File fb = new File(root, "pruefung");
			File fb1 = new File(fb, folder);
			File fb2 = new File(fb1, id);
			if (fb2.exists()) {
				f = fb2;

			} else {
				if (null == bearbeitFolder) {
					StringBuffer sb = new StringBuffer();
					if (development) {
						sb.append("user folder analyzed (in the DEVELOPMENT process):\r\n");
					} else {
						sb.append("user folder analyzed:\r\n");
					}
					bearbeitFolder = new LinkedList<>();
					File fc = new File(root, "bearbeit");
					for (File sub : fc.listFiles()) {
						if (sub.isDirectory()) {
							int size = (development ? 999 : sub.listFiles().length);
							if (0 < size) {
								if (0 < bearbeitFolder.size()) {
									sb.append("\r\n");
								}
								bearbeitFolder.add(sub);
								sb.append("\t").append(sub.getAbsolutePath());
								sb.append(" (").append(size).append(")");
							}
						}
					}
					LOG.info(sb.toString());
				}
				userFolder: for (File s : bearbeitFolder) {
					File fc2 = new File(s, id + "." + erw);
					if (fc2.exists()) {
						f = fc2;
						break userFolder;
					}
				}
				// Archiv --- ABSOLUTE NOTLOESUNG ---
				if (null == f) {
					File fd = new File(root, "archiv");
					File fd1 = new File(fd, folder);
					File fd2 = new File(fd1, id + ".1");
					if (fd2.exists()) {
						f = fd2;
					}
				}
			}
		}

		LOG.fine(id + " ~ " + folder + " ~ " + (null == f ? "null" : f.getAbsolutePath()));

		return f;
	}

	@Override
	public String toString() {
		return "D3ExSourceFolder [root=" + root.getAbsolutePath() + "]";
	}

}
