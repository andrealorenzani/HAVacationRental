package name.lorenzani.andrea.homeaway.json

import name.lorenzani.andrea.homeaway.datastore._
import org.scalatest.{FlatSpec, Matchers}

// No need of a Red Phase - I copied this class, I guess I have just to test it
class JsonUtilTest extends FlatSpec with Matchers {

  "JsonUtil" should "convert maps into json and json into maps" in {
    val map = Map[String, String]("a" -> "b", "c" -> "d")
    val json = JsonUtil.toJson(map)
    json should be ("{\"a\":\"b\",\"c\":\"d\"}")
    val reconverted = JsonUtil.fromJson[Map[String, String]](json)
    reconverted.get("a") should be (Some("b"))
    reconverted.get("c") should be (Some("d"))
    reconverted.size should be (2)
  }

  it should "convert case classes into json and json into case classes" in {
    val size = Size(3)
    var json = JsonUtil.toJson(size)
    json should be ("{\"size\":3}")
    val sizeReconverted = JsonUtil.fromJson[Size](json)
    sizeReconverted.size should be (3)

    val location = Location(1.666d, -3.666d)
    json = JsonUtil.toJson(location)
    json should be ("{\"lat\":1.666,\"lng\":-3.666}")
  }

  it should "consider Optional values" in {
    val address = Address("street", "BR10NE", None, Some("City"), None, "UK")
    var json = JsonUtil.toJson(address)
    json should be ("{\"address\":\"street\",\"postalCode\":\"BR10NE\",\"countryCode\":null,\"city\":\"City\",\"state\":null,\"country\":\"UK\"}")
    val addressReconverted = JsonUtil.fromJson[Address](json)
    addressReconverted.address should be ("street")
    addressReconverted.postalCode should be ("BR10NE")
    addressReconverted.countryCode should be (None)
    addressReconverted.city should be (Some("City"))
    addressReconverted.state should be (None)
    addressReconverted.country should be ("UK")
  }

  it should "consider Optional values in input" in {
    var json = "{\"address\":\"street\",\"postalCode\":\"BR10NE\",\"city\":\"City\",\"country\":\"UK\"}"
    val addressReconverted = JsonUtil.fromJson[Address](json)
    addressReconverted.address should be ("street")
    addressReconverted.postalCode should be ("BR10NE")
    addressReconverted.countryCode should be (None)
    addressReconverted.city should be (Some("City"))
    addressReconverted.state should be (None)
    addressReconverted.country should be ("UK")
  }

  it should "be able to handle out ListingWrapper" in {
    val address = Address("street", "BR10NE", None, Some("City"), None, "UK")
    val listing = Listing(Some("myid"), None, address, None)
    val lwrap = ListingWrapper(listing)
    var json = JsonUtil.toJson(lwrap)
    json should be ("{\"listing\":{\"id\":\"myid\",\"contact\":null,\"address\":{\"address\":\"street\",\"postalCode\":\"BR10NE\",\"countryCode\":null,\"city\":\"City\",\"state\":null,\"country\":\"UK\"},\"location\":null}}")
  }

}

