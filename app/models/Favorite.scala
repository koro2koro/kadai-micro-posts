package models

import java.time.ZonedDateTime

import scalikejdbc._
import jsr310._
import skinny.orm._
import skinny.orm.feature._

case class Favorite(id: Option[Long],
                      userId: Long,
                      micropostId: Long,
                      createAt: ZonedDateTime = ZonedDateTime.now(),
                      updateAt: ZonedDateTime = ZonedDateTime.now(),
                      user: Option[User] = None,
                      microPost: Option[MicroPost] = None)

object Favorite extends SkinnyCRUDMapper[Favorite] {

  lazy val u1 = User.createAlias("u1")

  lazy val userRef = belongsToWithAliasAndFkAndJoinCondition[User](
    right = User -> u1,
    fk = "userId",
    on = sqls.eq(defaultAlias.userId, u1.id),
    merge = (uf, f) => uf.copy(user = f)
  )

  lazy val u2 = MicroPost.createAlias("u2")

  lazy val followRef = belongsToWithAliasAndFkAndJoinCondition[MicroPost](
    right = MicroPost -> u2,
    fk = "micropostId",
    on = sqls.eq(defaultAlias.micropostId, u2.id),
    merge = (uf, f) => uf.copy(microPost = f)
  )

  lazy val allAssociations: CRUDFeatureWithId[Long, Favorite] = joins(userRef, followRef)

  override def tableName = "favorites"

  override def columnNames = Seq("id", "user_id", "micropost_id", "create_at", "update_at")

  override def defaultAlias: Alias[Favorite] = createAlias("uf")

  override def extract(rs: WrappedResultSet, n: ResultName[Favorite]): Favorite =
    autoConstruct(rs, n, "user", "microPost")

  def create(favorite: Favorite)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValues(favorite): _*)

  private def toNamedValues(record: Favorite): Seq[(Symbol, Any)] = Seq(
    'userId   -> record.userId,
    'micropostId -> record.micropostId,
    'createAt -> record.createAt,
    'updateAt -> record.updateAt
  )

  def update(favorite: Favorite)(implicit session: DBSession): Int =
    updateById(favorite.id.get).withAttributes(toNamedValues(favorite): _*)

}