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
