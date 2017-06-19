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
import name.lorenzani.andrea.homeaway.json.JsonUtil
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class PostRequestHandlerTest extends FlatSpec with Matchers {

  private val ds = new SimpleMapStore
  val rh = new PostRequestHandler(ds)

  behavior of "PostRequestHandlerTest"

  it should "serve requests for /new" in{
    val req = Request(Method.Post, "/new")
    req.contentString = "{\"listing\":{\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":null,\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}"
    val result = rh.handle(req)
    val content = for { res <- result
                        if res.status == Status.Ok
                        content = res.contentString }
      yield content
    val res = Await.result(content)
    val value = JsonUtil.fromJson[Map[String, String]](res).get("newId")
    value shouldNot be (None)
  }

  it should "reply 400 if you send an id" in{
    val req = Request(Method.Post, "/new")
    req.contentString = "{\"listing\":{\"id\":\"anyid\",\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":null,\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}"
    val result = Try{ rh.handle(req) }
    result.isFailure should be (true)
  }

  it should "send BadRequest othrwise" in{
    val result = Try{ rh.handle(Request(Method.Post, "/")) }
    result.isFailure should be (true)
  }

}
