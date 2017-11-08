package bob.d3.d3ext;

public class D3ExException extends Exception {

	private D3ExException(String msg, Throwable ex) {
		super(msg, ex);
	}

	public static class ConfigException extends D3ExException {

		public ConfigException(String msg, Throwable ex) {
			super(msg, ex);
		}

	}

	public static class ResourceException extends D3ExException {

		public ResourceException(String msg, Throwable ex) {
			super(msg, ex);
		}

	}

	public static class DatabaseException extends D3ExException {

		public DatabaseException(String msg, Throwable ex) {
			super(msg, ex);
		}

	}

}
