package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.DataStore

trait RequestHandler {
  def handle(request: Request): Future[Response]
}

object RequestHandler {

  def getGetHandler: RequestHandler = new GetRequestHandler(DataStore.getDataStore)
  def getPostHandler: RequestHandler = new PostRequestHandler(DataStore.getDataStore)
  def getDelHandler: RequestHandler = new DelRequestHandler(DataStore.getDataStore)
  def getPutHandler: RequestHandler = new PutRequestHandler(DataStore.getDataStore)
}

