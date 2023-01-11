package ir.ap.client.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WritableObjectProperty<T> extends SimpleObjectProperty<T> implements Serializable {

    public WritableObjectProperty() {
        super();
    }

    public WritableObjectProperty(T obj) {
        super(obj);
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeObject(get());
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        set((T) is.readObject());
    }
}
