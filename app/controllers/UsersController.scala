package controllers

import javax.inject._
import jp.t2v.lab.play2.auth.AuthenticationElement
import models.User
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.{FavoriteService, MicroPostService, UserFollowService, UserService}
import skinny.Pagination

@Singleton
class UsersController @Inject()(
                                 val userService: UserService,
                                 val microPostService: MicroPostService,
                                 val userFollowService: UserFollowService,
                                 val favoriteService: FavoriteService,
                                 components: ControllerComponents)
  extends AbstractController(components)
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  def index(page: Int): Action[AnyContent] = StackAction { implicit request =>
    userService.findAll(Pagination(pageSize = 2, pageNo = page))
      .map { users =>
        Ok(views.html.users.index(loggedIn, users))
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index())
            .flashing("failure" -> Messages("InternaError"))
      }
      .getOrElse(InternalServerError(Messages("InternaError")))
  }

  def show(userId: Long, page: Int) = StackAction { implicit request =>
    val triedUserOpt        = userService.findById(userId)
    val triedUserFollows    = userFollowService.findById(loggedIn.id.get)
    val pagination          = Pagination(10, page)
    val triedMicroPosts     = microPostService.findByUserId(pagination, userId)
    val triedFollowingsSize = userFollowService.countByUserId(userId)
    val triedFollowersSize  = userFollowService.countByFollowId(userId)
    val triedFavoriteSize   = favoriteService.countByUserId(userId)
    (for {
      userOpt        <- triedUserOpt
      userFollows    <- triedUserFollows
      microPosts     <- triedMicroPosts
      followingsSize <- triedFollowingsSize
      followersSize  <- triedFollowersSize
      favoriteSize   <- triedFavoriteSize
    } yield {
      val favoriteIds = favoriteService.findById(loggedIn.id.get).get.map(_.micropostId)
      userOpt.map { user =>
        Ok(views.html.users.show(loggedIn, user, userFollows, microPosts, followingsSize, followersSize, favoriteSize, favoriteIds))
      }.get
    }).recover {
      case e: Exception =>
        Logger.error(s"occurred error", e)
        Redirect(routes.UsersController.index())
          .flashing("failure" -> Messages("InternalError"))
    }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def showFavorite(userId: Long, page: Int) = StackAction { implicit request =>
    val triedUserOpt        = userService.findById(userId)
    val triedUserFollows    = userFollowService.findById(loggedIn.id.get)
    val pagination          = Pagination(10, page)
    val favoriteIds = favoriteService.findById(loggedIn.id.get).get.map(_.micropostId)
    val triedMicroPosts     = favoriteService.findAllByWithLimitOffset(pagination, userId)
    val triedFollowingsSize = userFollowService.countByUserId(userId)
    val triedFollowersSize  = userFollowService.countByFollowId(userId)
    val triedFavoriteSize   = favoriteService.countByUserId(userId)
    (for {
      userOpt        <- triedUserOpt
      userFollows    <- triedUserFollows
      microPosts     <- triedMicroPosts
      followingsSize <- triedFollowingsSize
      followersSize  <- triedFollowersSize
      favoriteSize   <- triedFavoriteSize
    } yield {
      userOpt.map { user =>
        Ok(views.html.users.show(loggedIn, user, userFollows, microPosts, followingsSize, followersSize, favoriteSize, favoriteIds))
      }.get
    }).recover {
      case e: Exception =>
        Logger.error(s"occurred error", e)
        Redirect(routes.UsersController.index())
          .flashing("failure" -> Messages("InternalError"))
    }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def getFollowers(userId: Long, page: Int) = StackAction { implicit request =>
    val targetUser           = User.findById(userId).get
    val triedMaybeUserFollow = userFollowService.findById(loggedIn.id.get)
    val pagination           = Pagination(10, page)
    val triedFollowers       = userFollowService.findFollowersByUserId(pagination, userId)
    val triedMicroPostsSize  = microPostService.countBy(userId)
    val triedFollowingsSize  = userFollowService.countByUserId(userId)
    val triedFavoriteSize    = favoriteService.countByUserId(userId)
    (for {
      userFollows    <- triedMaybeUserFollow
      followers      <- triedFollowers
      microPostSize  <- triedMicroPostsSize
      followingsSize <- triedFollowingsSize
      favoriteSize   <- triedFavoriteSize
    } yield {
      Ok(
        views.html.users.followers(
          loggedIn,
          targetUser,
          userFollows,
          followers,
          microPostSize,
          followingsSize,
          favoriteSize
        )
      )
    }).recover {
      case e: Exception =>
        Logger.error("occurred error", e)
        Redirect(routes.UsersController.index())
          .flashing("failure" -> Messages("InternalError"))
    }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def getFollowings(userId: Long, page: Int) = StackAction { implicit request =>
    val targetUser          = User.findById(userId).get
    val triedUserFollows    = userFollowService.findById(loggedIn.id.get)
    val pagination          = Pagination(10, page)
    val triedFollowings     = userFollowService.findFollowingsByUserId(pagination, userId)
    val triedMicroPostsSize = microPostService.countBy(userId)
    val triedFollowersSize  = userFollowService.countByFollowId(userId)
    val triedFavoriteSize   = favoriteService.countByUserId(userId)
    (for {
      userFollows    <- triedUserFollows
      followings     <- triedFollowings
      microPostsSize <- triedMicroPostsSize
      followersSize  <- triedFollowersSize
      favoriteSize   <- triedFavoriteSize
    } yield {
      Ok(
        views.html.users.followings(
          loggedIn,
          targetUser,
          userFollows,
          followings,
          microPostsSize,
          followersSize,
          favoriteSize
        )
      )
    }).recover {
      case e: Exception =>
        Logger.error("occurred error", e)
        Redirect(routes.UsersController.index())
          .flashing("failure" -> Messages("InternalError"))
    }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

}