package name.lorenzani.andrea.homeaway

import com.twitter.finagle.Http
import com.twitter.util.Await
import name.lorenzani.andrea.homeaway.services.RequestHandler._
import name.lorenzani.andrea.homeaway.services.RestService

object VacationRentals extends App {
  val restApi = new RestService(getGetHandler, getPostHandler, getDelHandler, getPutHandler)
  val server = Http.server
    .withLabel("VacationRental")
    .serve(":8080", restApi)
  Await.ready(server) // waits until the server resources are released
}
