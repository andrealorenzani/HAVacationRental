package name.lorenzani.andrea.homeaway.services
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.{DataStore, Size}
import name.lorenzani.andrea.homeaway.json.JsonUtil

import scala.util.{Failure, Success, Try}

class GetRequestHandler(ds: DataStore) extends RequestHandler {
  override def handle(request: Request): Future[Response] = {
    val rawResult = Path(request.path) match {
      case Root / "listings" => showAllListings()
      case Root / "listing" / id => showListingId(id)
      case Root / "size" => getSize
      case _ => throw new IllegalArgumentException("API request failure")
    }
    Future {
      val response = Response()
      rawResult match {
        case Success(res) => {
          response.setContentTypeJson()
          response.contentString = res
        }
        case Failure(ex) => {
          response.status = Status.BadRequest
          response.contentString = ex.getMessage
        }
      }
      response
    }
  }

  def showListingId(id: String): Try[String] = ds.get(id) match {
    case Some(res) => Success(JsonUtil.toJson(res))
    case None => Failure(new IllegalArgumentException("Not found"))
  }

  // Not required... but nice to have
  def showAllListings(): Try[String] = Try{JsonUtil.toJson(ds.getAll)}
  def getSize: Try[String] = Try{
    val size = ds.size
    JsonUtil.toJson(Size(size))
  }
}
