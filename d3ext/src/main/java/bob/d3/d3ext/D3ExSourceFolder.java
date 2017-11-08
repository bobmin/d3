package bob.d3.d3ext;

import java.io.File;
import java.util.Objects;

import bob.d3.d3ext.D3ExException.SourceException;

public class D3ExSourceFolder {

	private static final String SOURCE_PATH_KEY = "D3ExSourceFolder.source_path";

	private final File root;

	private D3ExSourceFolder(File root) {
		Objects.requireNonNull(root);
		this.root = root;
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

	public File lookFor(String id) throws SourceException {
		Objects.requireNonNull(id);
		String folder;
		if (8 == id.length()) {
			folder = id.substring(0, 4);
		} else if (10 == id.length()) {
			folder = id.substring(0, 6);
		} else {
			throw new D3ExException.SourceException("format from [id] is unknown: " + id, null);
		}
		File f0 = new File(root, "public");
		File f1 = new File(f0, folder);
		File f2 = new File(f1, id);
		System.out.println(id + " ~ " + folder + " ~ " + f2.exists());
		return null;
	}

	@Override
	public String toString() {
		return "D3ExSourceFolder [root=" + root.getAbsolutePath() + "]";
	}

}
