/***
*   Copyright 2017 Andrea Lorenzani
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*
***/

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
