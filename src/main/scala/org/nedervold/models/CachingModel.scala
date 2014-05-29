package org.nedervold.models

/**
 * Caches the latest value and publishes when the value changes.
 * @author nedervold
 */
abstract class CachingModel[T](initValue: T,
                               val elmtEquals: (T, T) => Boolean)
    extends Model[T] {
    private[this] var cachedValue = initValue;

    def value = cachedValue;
    protected def value_=(newValue: T) {
        if (!elmtEquals(newValue, cachedValue)) {
            cachedValue = newValue;
            publish(new ModelChangedEvent(this));
        }
    }
}