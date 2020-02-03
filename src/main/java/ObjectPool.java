import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public abstract class ObjectPool<T> implements ObjectFactory<T>, Pool<T> {

    private int size;

    private boolean shutdown;

    private BlockingQueue objects;

    public ObjectPool(int size) {
        this.size = size;
        shutdown = false;
        init();
    }

    /*
     * initiate the pool with fix size
     */
    private void init() {
        objects = new LinkedTransferQueue();
        for (int i = 0; i < size; i++) {
            objects.add(createNew());
        }
    }

    @Override
    public T get() {
        if (!shutdown) {
            T t = null;

            try {
                t = (T)objects.take();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return t;
        }

        throw new IllegalStateException("Object pool is already shutdown.");
    }

    @Override
    public void release(T t) {
        try {
            objects.offer(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        objects.clear();
    }

    public int size() {
        return objects.size();
    }

}