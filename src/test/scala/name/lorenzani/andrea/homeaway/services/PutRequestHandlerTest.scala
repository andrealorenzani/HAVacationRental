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

import com.twitter.finagle.http.{Method, Request, Status}
import com.twitter.util.Await
import name.lorenzani.andrea.homeaway.datastore._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class PutRequestHandlerTest extends FlatSpec with Matchers {

  private val ds = new SimpleMapStore
  val rh = new PutRequestHandler(ds)
  private val listing = ListingWrapper(Listing(None, None, Address("addr", "post", None, None, None, "UK"), None))
  private val lstkey = ds.add(listing)


  behavior of "PutRequestHandlerTest"

  it should "serve requests for /update" in{
    val req = Request(Method.Put, "/update")
    req.contentString = "{\"listing\":{\"id\":\""+lstkey+"\",\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":\"changed\",\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}"
    val result = rh.handle(req)
    val content = for { res <- result } yield res.status
    val res = Await.result(content)
    res should be (Status.Ok)

    ds.get(lstkey).isDefined should be (true)
    ds.get(lstkey).get.listing.address.countryCode should be (Some("changed"))
  }

  it should "reply 400 if you don't send an id" in{
    val req = Request(Method.Put, "/new")
    req.contentString = "{\"listing\":{\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":null,\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}"
    val result = Try{ rh.handle(req) }
    result.isFailure should be (true)
  }

  it should "send BadRequest othrwise" in{
    val result = Try{ rh.handle(Request(Method.Put, "/")) }
    result.isFailure should be (true)
  }

}

