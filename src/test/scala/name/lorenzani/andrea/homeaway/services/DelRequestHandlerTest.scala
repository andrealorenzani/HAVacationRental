package name.lorenzani.andrea.homeaway.services

import com.twitter.finagle.http.{Method, Request, Status}
import com.twitter.util.Await
import name.lorenzani.andrea.homeaway.datastore._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try


class DelRequestHandlerTest extends FlatSpec with Matchers {

  private val ds = new SimpleMapStore
  val rh = new DelRequestHandler(ds)
  private val listing = ListingWrapper(Listing(None, None, Address("addr", "post", None, None, None, "UK"), None))
  private val key1 = ds.add(listing)
  private val key2 = ds.add(listing)
  private val key3 = ds.add(listing)

  behavior of "DelRequestHandlerTest"

  it should "serve requests for /del and /delall" in{
    val req = Request(Method.Delete, s"/del/$key3")
    val result = rh.handle(req)
    val content = for (res <- result) yield (res.status, res.contentString)
    val res = Await.result(content)
    res._1 should be (Status.Ok)
    val expected = "{\"listing\":{\"id\":\""+key3+"\",\"contact\":null,\"address\":{\"address\":\"addr\",\"postalCode\":\"post\",\"countryCode\":null,\"city\":null,\"state\":null,\"country\":\"UK\"},\"location\":null}}"
    res._2 should be (expected)
    ds.size should be (2)

    val reqAll = Request(Method.Delete, s"/delall")
    val resultAll = rh.handle(reqAll)
    val contentAll = for (res <- resultAll) yield res.status
    val resAll = Await.result(contentAll)
    resAll should be (Status.Ok)
    ds.size should be (0)

    val reqNothing = Request(Method.Delete, s"/del/id")
    val resultN = rh.handle(reqNothing)
    val contentN = for (res <- resultN) yield res.status
    val resN = Await.result(contentN)
    resN should be (Status.BadRequest)

  }

  it should "send BadRequest othrwise" in{
    val result = Try{ rh.handle(Request(Method.Delete, "/")) }
    result.isFailure should be (true)
  }
}
