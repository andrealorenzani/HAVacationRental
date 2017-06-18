package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.Service
import com.twitter.finagle.http._
import com.twitter.logging.Logger
import com.twitter.util.Future

import scala.util.{Failure, Success, Try}

//https://gist.github.com/stonegao/1273845

class RestService (getHandler: RequestHandler,
                   postHandler: RequestHandler,
                   delHandler: RequestHandler,
                   putHandler: RequestHandler
                  ) extends Service[Request, Response] {
  val logger = Logger.get(this.getClass)

  def apply(request: Request): Future[Response] = {
    val reply = Try {
      request.method match {
        case Method.Get => getHandler.handle(request)
        case Method.Post => postHandler.handle(request)
        case Method.Delete => delHandler.handle(request)
        case Method.Put => putHandler.handle(request)
        case _ => Future value Response(Version.Http11, Status.NotFound)
      }
    }
    reply match {
      case Success(value) => value
      case Failure(reason) => Future.value {
        val message = Option(reason.getMessage) getOrElse "Something went wrong."
        logger.error(reason, message)
        val res = Response(Version.Http11, Status.InternalServerError)
        res.setContentString(reason.getMessage)
        res
      }
    }
  }
}