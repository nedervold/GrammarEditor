package org.nedervold.grammareditor.models

/**
 * A concrete [[VariableModel]].
 *
 * @param initValue the initial value
 * @param elmtEquals a predicate to define whether the value has changed
 */
class VarModel[T](initValue: T,
                  elmtEquals: (T, T) => Boolean = ((lhs: T, rhs: T) => lhs == rhs))
    extends CachingModel[T](initValue, elmtEquals) with VariableModel[T] {
    override def value_=(newValue: T) = super.value_=(newValue);
}