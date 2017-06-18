package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.DataStore

class PostRequestHandler(ds: DataStore) extends RequestHandler {
  override def handle(request: Request): Future[Response] = ???
}
