package org.nedervold.grammareditor.models

import java.util.concurrent.TimeUnit

/**
 * Models the base [[Model]]'s value, but smoothing out changes.
 *
 * This model's value will not change until the base model's value has been stable for the
 * delay defined in the constructor.
 *
 * @constructor
 * @param baseModel the base [[Model]]
 * @param time the number of time units to debounce
 * @param unit the time unit
 * @param elmtEquals the predicate to define whether the value has changed
 * @author nedervold
 */
class DebouncingModel[T](val baseModel: Model[T],
                         val time: Long,
                         val unit: TimeUnit,
                         override val elmtEquals: (T, T) => Boolean = ((lhs: T, rhs: T) => lhs == rhs))
    extends CachingModel[T](baseModel.value, elmtEquals) {
    require(unit != null)
    private val delayMillis = unit.toMillis(time);

    // TODO This isn't very Scalesque, but it'll do.
    private class FireAction extends Thread {
        override def run() = {
            try {
                Thread.sleep(delayMillis);
                fireAction(this);
            } catch {
                // I was replaced.
                case _: InterruptedException => ()
            }
        }
    }

    private var latestAction: FireAction = null;

    private def installAction() = {
        this.synchronized {
            if (latestAction != null) {
                latestAction.interrupt();
            }
            latestAction = new FireAction();
            innerBouncingModel.value = true;
            latestAction.start();
        }
    }

    private def fireAction(action: FireAction) = {
        this.synchronized {
            if (action == latestAction) {
                // I won!
                value = baseModel.value;
                innerBouncingModel.value = false;
                latestAction = null;
            } else {
                // I was replaced; do nothing
            }
        }
    }

    reactions += {
        case ModelChangedEvent(_) => installAction()
    }
    listenTo(baseModel);

    private val innerBouncingModel = new VarModel(false);

    /**
     * A read-only [[Model]] returning true iff the base model's value is still changing.
     */
    val bouncingModel: Model[Boolean] = innerBouncingModel

}