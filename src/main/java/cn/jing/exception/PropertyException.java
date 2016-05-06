package cn.jing.exception;

/**
 * Created by dubby on 16/5/5.
 */
public class PropertyException extends IllegalArgumentException {

    public PropertyException() {
        super("configuration file has error.");
    }
}
