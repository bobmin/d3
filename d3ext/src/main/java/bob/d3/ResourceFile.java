package bob.d3;

import java.io.InputStream;
import java.util.Scanner;

import bob.d3.D3ExException.ResourceException;

public class ResourceFile {

	private static final String CHARSET = "UTF8";

	private static final String LINE_SEPARATOR = "\n";

	private final String content;

	public ResourceFile(final String path) throws ResourceException {
		if (null == path) {
			throw new D3ExException.ResourceException("[path] may not NULL", null);
		}
		this.content = read(path);
	}

	private String read(final String path) throws ResourceException {
		StringBuilder sb = null;
		final InputStream io = ResourceFile.class.getResourceAsStream(path);
		if (null == io) {
			throw new D3ExException.ResourceException("[path] cannot found: " + path, null);
		}
		sb = new StringBuilder();
		final Scanner scanner;
		scanner = new Scanner(io, CHARSET);
		while (scanner.hasNextLine()) {
			if (0 < sb.length()) {
				sb.append(LINE_SEPARATOR);
			}
			sb.append(scanner.nextLine());
		}
		scanner.close();
		return sb == null ? null : sb.toString();
	}

	public String getText() {
		return content;
	}

}
