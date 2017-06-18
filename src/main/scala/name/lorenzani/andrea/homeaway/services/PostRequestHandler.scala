package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.path._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.{DataStore, ListingWrapper}
import name.lorenzani.andrea.homeaway.json.JsonUtil

class PostRequestHandler(ds: DataStore) extends RequestHandler {
  override def handle(request: Request): Future[Response] = {
    val newid = Path(request.path) match {
      case Root / "new" => putListing(request)
      case _ => throw new IllegalArgumentException("API request failure")
    }
    Future {
      val response = Response()
      val content = JsonUtil.toJson(Map("newId" -> newid))
      response.setContentString(content)
      response
    }
  }

  def putListing(req: Request): String = {
    val content = req.contentString
    val listing = JsonUtil.fromJson[ListingWrapper](content)
    ds.add(listing)
  }
}
