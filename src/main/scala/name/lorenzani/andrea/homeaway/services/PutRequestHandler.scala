package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.path.{/, Path, Root}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.{DataStore, ListingWrapper}
import name.lorenzani.andrea.homeaway.json.JsonUtil

class PutRequestHandler(ds: DataStore) extends RequestHandler {
  override def handle(request: Request): Future[Response] = {
    val newid = Path(request.path) match {
      case Root / "update" => putListing(request)
      case _ => throw new IllegalArgumentException("API request failure")
    }
    Future {
      val response = Response()
      response
    }
  }

  def putListing(req: Request): Unit = {
    val content = req.contentString
    val listing = JsonUtil.fromJson[ListingWrapper](content)
    ds.update(listing)
  }
}
