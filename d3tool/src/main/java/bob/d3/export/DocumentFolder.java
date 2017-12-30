package bob.d3.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import bob.d3.D3Config;
import bob.d3.D3ExException.SourceException;

public class DocumentFolder {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(DocumentFolder.class.getName());

	private final File root;

	private List<File> bearbeitFolder = null;

	private final boolean development;

	private DocumentFolder(File root) {
		Objects.requireNonNull(root);
		this.root = root;
		development = D3Config.getDefault().isDevelomentActiv();
	}

	/**
	 * Erstellt eine neue Instanz der Dokumentenablage, die durchsucht werden
	 * kann.
	 * 
	 * @param root
	 *            das Wurzelverzeichnis der Dokumentenablage
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws FileNotFoundException
	 *             wenn Wurzelverzeichnis nicht existiert
	 */
	public static DocumentFolder create(final File root) throws FileNotFoundException {
		Objects.requireNonNull(root);
		if (!root.exists()) {
			throw new FileNotFoundException("[root] does not exist: " + root.getAbsolutePath());
		}
		DocumentFolder x = new DocumentFolder(root);
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
