package jard.alchym.api.transmutation;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/***
 *  TransmutationInterface
 *  An interface that specifies how a TransmutationAction should interact with the world.
 *
 *  Essentially, this interface comprises of three objects:
 *  - a K instance which represents anything that can be a source and sink (the endpoint),
 *  - a BiConsumer which accepts an arbitrary T instance and K instance (usually, the endpoint) and pushes it to the
 *    endpoint (the pushing 'channel'), and
 *  - a BiConsumer which accepts an arbitrary T instance and K instance (usually, the endpoint) attempts to pull that
 *    object from the endpoint (the pulling 'channel').
 *
 *  In addition to these, a BiPredicate is also required which 'peeks' into the endpoint to see if a T instance exists
 *  within the endpoint. This ensures that when pull::accept is called by extract, all T instances that are supplied to
 *  it are guaranteed to exist, so the actual pulling logic can be simplified.
 *
 *  Created by jard at 5:24 PM on April 18, 2019.
 ***/
public abstract class TransmutationInterface <T, K> {
    private final BiConsumer<T, K> CLOSED_CHANNEL = ($, $end) -> {};

    K endpoint;

    BiConsumer<T, K> push;
    BiConsumer<T, K> pull;

    BiPredicate<T, K> peek;

    public TransmutationInterface (K endpoint, BiConsumer<T, K> pusher, BiConsumer<T, K> puller, BiPredicate <T, K> peeker) {
        this.endpoint = endpoint;
        push = pusher;
        pull = puller;
        peek = peeker;
    }

    final TransmutationInterface closePushChannel () {
        push = CLOSED_CHANNEL;
        return this;
    }

    final TransmutationInterface closePullChannel () {
        pull = CLOSED_CHANNEL;
        peek = ($, $end) -> false;
        return this;
    }

    public final TransmutationInterface close () {
        return closePushChannel ().closePullChannel ();
    }

    final void insert (T ... instances) {
        for (T instance : instances)
            push.accept (instance, endpoint);
    }

    final boolean extract (T ... instances) {
        // First, peek through all instances to verify that every instance exists in the endpoint.
        for (T instance : instances) {
            if (!peek.test (instance, endpoint))
                return false;
        }

        // Then, go through all instances and pull them.
        for (T instance : instances) {
            pull.accept (instance, endpoint);
        }

        return true;
    }
}
