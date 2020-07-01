package nc.uap.studio.pub.db.exception;

public class DatabaseRuntimeException extends RuntimeException {
    public DatabaseRuntimeException(String string) {
        super(string);
    }

    public DatabaseRuntimeException(String string, Exception e) {
        super(string, e);
    }
}
