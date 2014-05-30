package org.nedervold.grammareditor

/**
 * A grammar of grammars
 *
 * @author nedervold
 */
package object grammar {
    /**
     * Alternates (joins with [[Or]]) the given terms into a single one.
     * @param ts the terms to alternate
     * @return an alternated term or [[Fail]]
     */
    def alternate(ts: Term*): Term = { alternate(ts.toList) }

    /**
     * Alternates (joins with [[Or]]) the given terms into a single one.
     * @param ts the terms to alternate
     * @return an alternated term or [[Fail]]
     */
    def alternate(ts: List[Term]): Term = {
        ts match {
            case Nil => Fail
            case t :: Nil => t
            case t :: ts => Or(t, alternate(ts))
        }
    }

    /**
     * Sequences the given terms into a single one.
     * @param ts the terms to sequence
     * @return a sequenced term or [[Epsilon]]
     */
    def sequence(ts: Term*): Term = { sequence(ts.toList) }

    /**
     * Sequences the given terms into a single one.
     * @param ts the terms to sequence
     * @return a sequenced term or [[Epsilon]]
     */
    def sequence(ts: List[Term]): Term = {
        // TODO 2014-05-29 Do I want to break up leading Sequenceds?
        ts match {
            case Nil => Epsilon
            case t :: Nil => t
            case t :: ts => Sequenced(t, sequence(ts))
        }
    }

}