package services

import javax.inject.Singleton
import models._
import scalikejdbc._
import skinny.Pagination

import scala.util.Try

@Singleton
class FavoriteServiceImpl extends FavoriteService {

  override def create(favorite: Favorite)(implicit dbSession: DBSession): Try[Long] = Try {
    Favorite.create(favorite)
  }

  override def findById(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[Favorite]] = Try {
    Favorite.where('userId -> userId).apply()
  }

  override def findByMicropostId(micropostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[Favorite]] =
    Try {
      Favorite.where('micropostId -> micropostId).apply().headOption
    }

  // userIdのユーザーをフォローするユーザーの集合を取得する
  override def findMicropostsByUserId(pagination: Pagination, micropostId: Long)(implicit dbSession: DBSession = AutoSession): Try[PagedItems[User]] = {
    countByMicropostId(micropostId).map { size =>
      PagedItems(pagination, size,
        Favorite.allAssociations.findAllByWithLimitOffset(
            sqls.eq(Favorite.defaultAlias.micropostId, micropostId),
            pagination.limit,
            pagination.offset,
            Seq(Favorite.defaultAlias.id.desc)
          ).map(_.user.get)
      )
    }
  }

  override def countByMicropostId(micropostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try {
    Favorite.allAssociations.countBy(sqls.eq(Favorite.defaultAlias.micropostId, micropostId))
  }

  // userIdのユーザーがフォローしているユーザーの集合を取得する
  override def findFavoriteByUserId(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession = AutoSession
  ): Try[PagedItems[MicroPost]] = {
    // 全体の母数を取得する
    countByUserId(userId).map { size =>
      PagedItems(pagination, size,
        Favorite.allAssociations
          .findAllByWithLimitOffset(
            sqls.eq(Favorite.defaultAlias.userId, userId),
            pagination.limit,
            pagination.offset,
            Seq(Favorite.defaultAlias.id.desc)
          )
          .map(_.microPost.get)
      )
    }
  }

  override def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try {
    Favorite.allAssociations.countBy(sqls.eq(Favorite.defaultAlias.userId, userId))
  }

  override def deleteBy(userId: Long, micropostId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int] = Try {
    val c     = Favorite.column
    val count = Favorite.countBy(sqls.eq(c.userId, userId).and.eq(c.micropostId, micropostId))
    if (count == 1) {
      Favorite.deleteBy(
        sqls
          .eq(Favorite.column.userId, userId)
          .and(sqls.eq(Favorite.column.micropostId, micropostId))
      )
    } else 0
  }

  override def findAllByWithLimitOffset(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession
  ): Try[PagedItems[MicroPost]] = Try {
    val favoritePostIds: Seq[Long] = Favorite.findAllBy(sqls.eq(Favorite.defaultAlias.userId, userId)).map(_.micropostId)
    //val size = MicroPost.countBy(sqls.in(MicroPost.defaultAlias.userId, favoriteIds))
    PagedItems(pagination, favoritePostIds.size, findAllByWithLimitOffset(favoritePostIds)(pagination))
  }

  private def findAllByWithLimitOffset(favoritePostIds: Seq[Long])(pagination: Pagination)(
    implicit dbSession: DBSession
  ): Seq[MicroPost] = MicroPost.findAllByWithLimitOffset(
    sqls.in(MicroPost.defaultAlias.id, favoritePostIds),
    pagination.limit,
    pagination.offset,
    Seq(MicroPost.defaultAlias.id.desc)
  )




}