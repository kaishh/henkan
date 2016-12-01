package henkan.optional

import cats.{Eval, Applicative, Traverse}

/**
 * A superficial seq instance that only works efficiently if the underling implementation is either Vector or List
 * This is mostly created for dealing with the Seq fields generated by scalaPB for repeated items.
 */
trait SeqInstance {
  import cats.instances.vector._
  import cats.instances.list._
  import cats.syntax.traverse._
  import cats.syntax.functor._
  /**
   * this is created for Seq with underlying implementation being List or Vector
   */
  implicit val henkanTraverseInstanceForSeq: Traverse[Seq] = new Traverse[Seq] {
    override def traverse[G[_], A, B](fa: Seq[A])(f: (A) ⇒ G[B])(implicit G: Applicative[G]): G[Seq[B]] =
      fa match {
        case v: Vector[A] ⇒ v.traverse(f).widen
        case l: List[A]   ⇒ l.traverse(f).widen
        case _            ⇒ fa.toList.traverse(f).widen
      }

    override def foldLeft[A, B](fa: Seq[A], b: B)(f: (B, A) ⇒ B): B = fa.foldLeft(b)(f)

    override def foldRight[A, B](fa: Seq[A], lb: Eval[B])(f: (A, Eval[B]) ⇒ Eval[B]): Eval[B] = {
      fa match {
        case v: Vector[A] ⇒ v.foldRight(lb)(f).widen
        case l: List[A]   ⇒ l.foldRight(lb)(f).widen
        case _            ⇒ fa.toList.foldRight(lb)(f).widen
      }
    }
  }
}
