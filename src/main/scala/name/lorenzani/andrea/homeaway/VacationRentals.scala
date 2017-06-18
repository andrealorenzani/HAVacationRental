package name.lorenzani.andrea.homeaway

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await

// Repository: https://github.com/finagle/finch
// Article: https://finagle.github.io/blog/2014/12/10/rest-apis-with-finch/
// Homepage: http://finagle.github.io/finch/
// Finagle HP: https://twitter.github.io/finagle/guide/Quickstart.html
// Examples: https://github.com/finagle/finch/tree/master/examples

object VacationRentals extends App {
  /*val server = Http.server
    .withLabel("VacationRental")
    .serve(":8080", restApi)
  Await.ready(server) // waits until the server resources are released*/
}
