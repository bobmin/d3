package bob.d3.d3ext;

import java.io.InputStream;
import java.util.Scanner;

import bob.d3.d3ext.D3ExException.ResourceException;

public class D3ExResource {

	private static final String CHARSET = "UTF8";

	private static final String LINE_SEPARATOR = "\n";

	private final String content;

	public D3ExResource(final String path) throws ResourceException {
		if (null == path) {
			throw new D3ExException.ResourceException("[path] may not NULL", null);
		}
		this.content = read(path);
	}

	private String read(final String path) throws ResourceException {
		StringBuilder sb = null;
		final InputStream io = D3ExResource.class.getResourceAsStream(path);
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
