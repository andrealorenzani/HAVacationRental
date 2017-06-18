package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http._
import com.twitter.util.Await
import name.lorenzani.andrea.homeaway.datastore._
import name.lorenzani.andrea.homeaway.json.JsonUtil
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class GetRequestHandlerTest extends FlatSpec with Matchers {

  private val ds = new SimpleMapStore
  private val listing = Listing(None, None, Address("addr", "post", None, None, None, "UK"), None)
  val key1 = ds.add(ListingWrapper(listing))
  val key2 = ds.add(ListingWrapper(listing))
  val rh = new GetRequestHandler(ds)

  behavior of "GetRequestHandlerTest"

  it should "serve requests for /listing/<someid>" in{
    val result = rh.handle(Request(Method.Get, s"/listing/$key1"))
    val content = for { res <- result
                        if res.status == Status.Ok
                        content = res.contentString }
                  yield content
    val res = Await.result(content)
    res should be ("{\"listing\":{\"id\":\""+key1+"\",\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":null,\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}")

    val notfound = rh.handle(Request(Method.Get, "/listing/noid"))
    val contentFail = for { res <- notfound
                        if res.status == Status.BadRequest
                        content = res.contentString }
      yield content
    val newres = Await.result(contentFail)
    newres should be ("Not found")

  }

  it should "eventually serve requests for /listings" in{
    val result = rh.handle(Request(Method.Get, "/listings"))
    val content = for { res <- result
                        if res.status == Status.Ok
                        content = res.contentString }
      yield content
    val res = Await.result(content)
    JsonUtil.fromJson[List[ListingWrapper]](res).size should be (2)
  }

  it should "eventually serve requests for /size" in{
    val result = rh.handle(Request(Method.Get, "/size"))
    val content = for { res <- result
                        if res.status == Status.Ok
                        content = res.contentString }
      yield content
    val res = Await.result(content)
    res should be ("{\"size\":2}")
  }

  it should "send BadRequest othrwise" in{
    val result = Try{ rh.handle(Request(Method.Get, "/")) }
    result.isFailure should be (true)
  }

}
