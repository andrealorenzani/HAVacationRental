package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.path.{/, Path, Root}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.DataStore
import name.lorenzani.andrea.homeaway.json.JsonUtil

import scala.util.{Failure, Success, Try}

class DelRequestHandler(ds: DataStore) extends RequestHandler {
  override def handle(request: Request): Future[Response] = {
    val rawMessage = Path(request.path) match {
      case Root / "delall" => deleteAllListings()
      case Root / "del" / id => deleteListing(id)
      case _ => throw new IllegalArgumentException("API request failure")
    }
    Future {
      val response = Response()
      rawMessage match {
        case Success(msg) => response.setContentString (msg)
        case Failure(e) => {
          response.status = Status.BadRequest
          response.setContentString("Not found")
        }
      }
      response
    }
  }

  def deleteListing(id: String): Try[String] = Try { JsonUtil.toJson(ds.del(id)) }
  def deleteAllListings(): Try[String] = Try { JsonUtil.toJson(ds.delAll) }
}
