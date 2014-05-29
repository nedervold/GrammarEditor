package org.nedervold.grammareditor.models

/**
 * Models the result of running a unary function on the base [[Model]]'s value.
 *
 * The name 'fmap' comes from Haskell.
 *
 * @author nedervold
 *
 * @tparam S type of the base [[Model]]
 * @tparam R type of this model
 *
 * @constructor
 * @param f the unary function
 * @param baseModel the base [[Model]]
 *
 */
class FmapModel[S, R](val f: S => R,
                      val baseModel: Model[S])
    extends CachingModel[R](f(baseModel.value), _ == _) {
    reactions += {
        case ModelChangedEvent(_) =>
            value = f(baseModel.value);
    }
    listenTo(baseModel);
    // called to catch any missed changes since the call of the parent constructor
    value = f(baseModel.value);
}

/**
 * Models the result of running a binary function on the base [[Model]]s' values.
 * @author nedervold
 *
 * @tparam S type of the first base [[Model]]
 * @tparam T type of the second base [[Model]]
 * @tparam R type of this model
 *
 * @constructor
 * @param f the binary function
 * @param baseModel the first base [[Model]]
 * @param baseModel2 the second base [[Model]]
 */
class Fmap2Model[S, T, R](val f: (S, T) => R,
                          val baseModel1: Model[S],
                          val baseModel2: Model[T])
    extends CachingModel[R](f(baseModel1.value, baseModel2.value), _ == _) {
    reactions += {
        case ModelChangedEvent(_) =>
            value = f(baseModel1.value, baseModel2.value);
    }
    listenTo(baseModel1);
    listenTo(baseModel2);
    // called to catch any missed changes since the call of the parent constructor
    value = f(baseModel1.value, baseModel2.value);
}

/**
 * Models the result of running a  function of arity 3 on the base [[Model]]s' values.
 * @author nedervold
 *
 * @tparam S type of the first base [[Model]]
 * @tparam T type of the second base [[Model]]
 * @tparam U type of the third base [[Model]]
 * @tparam R type of this model
 *
 * @constructor
 * @param f the  function of arity 3
 * @param baseModel the first base [[Model]]
 * @param baseModel2 the second base [[Model]]
 * @param baseModel3 the third base [[Model]]
 */
class Fmap3Model[S, T, U, R](val f: (S, T, U) => R,
                             val baseModel1: Model[S],
                             val baseModel2: Model[T],
                             val baseModel3: Model[U])
    extends CachingModel[R](f(baseModel1.value, baseModel2.value, baseModel3.value), _ == _) {
    reactions += {
        case ModelChangedEvent(_) =>
            value = f(baseModel1.value, baseModel2.value, baseModel3.value);
    }
    listenTo(baseModel1);
    listenTo(baseModel2);
    listenTo(baseModel3);
    // called to catch any missed changes since the call of the parent constructor
    value = f(baseModel1.value, baseModel2.value, baseModel3.value);
}

/**
 * Models the result of running a  function of arity 4 on the base [[Model]]s' values.
 * @author nedervold
 *
 * @tparam S type of the first base [[Model]]
 * @tparam T type of the second base [[Model]]
 * @tparam U type of the third base [[Model]]
 * @tparam V type of the fourth base [[Model]]
 * @tparam R type of this model
 *
 * @constructor
 * @param f the  function of arity 4
 * @param baseModel the first base [[Model]]
 * @param baseModel2 the second base [[Model]]
 * @param baseModel3 the third base [[Model]]
 * @param baseModel4 the fourth base [[Model]]
 */
class Fmap4Model[S, T, U, V, R](val f: (S, T, U, V) => R,
                                val baseModel1: Model[S],
                                val baseModel2: Model[T],
                                val baseModel3: Model[U],
                                val baseModel4: Model[V])
    extends CachingModel[R](f(baseModel1.value, baseModel2.value, baseModel3.value, baseModel4.value), _ == _) {
    reactions += {
        case ModelChangedEvent(_) =>
            value = f(baseModel1.value, baseModel2.value, baseModel3.value, baseModel4.value);
    }
    listenTo(baseModel1);
    listenTo(baseModel2);
    listenTo(baseModel3);
    listenTo(baseModel4);
    // called to catch any missed changes since the call of the parent constructor
    value = f(baseModel1.value, baseModel2.value, baseModel3.value, baseModel4.value);
}