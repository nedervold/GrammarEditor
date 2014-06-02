package org.nedervold.grammareditor.models

import java.util.concurrent.TimeUnit

/**
 * A model whose value is determined by polling some entity.
 * @author nedervold
 *
 * @constructor
 * @param pollForValue polls the entity for the value
 * @param time the number of time units to poll the entity
 * @param unit the time unit
 * @param elmtEquals the predicate to define whether the value has changed
 */
class PolledModel[T](val pollForValue: () => T,
                     val time: Long,
                     val unit: TimeUnit,
                     override val elmtEquals: (T, T) => Boolean = ((lhs: T, rhs: T) => lhs == rhs))
    extends CachingModel[T](pollForValue(), elmtEquals) {

    private val delayMillis = unit.toMillis(time);

    private val pollingThread = new Thread {
        override def run = {
            while (true) {
                Thread.sleep(delayMillis)
                value = pollForValue()
            }
        }
    }
    pollingThread.setDaemon(true)
    pollingThread.start
}