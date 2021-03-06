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

package name.lorenzani.andrea.homeaway.datastore

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SimpleMapStoreTest extends FlatSpec with BeforeAndAfterEach with Matchers {

  var sms = new SimpleMapStore

  override def beforeEach() {
    sms = new SimpleMapStore
  }

  val listing = ListingWrapper(
    Listing(None,
      None,
      Address("addr", "BR10NE", None, None, None, "UK"),
      None
    )
  )

  behavior of "SimpleMapStoreTest"

  it should "update" in {
    val key = sms.add(listing)
    val contact = Contact("123", None)
    sms.update(ListingWrapper(listing.listing.copy(id=Some(key), contact=Some(contact))))
    sms.get(key).get.listing.contact should be (Some(contact))
  }

  it should "get" in {
    sms.get("notexisting") should be (None)
    val key = sms.add(listing)
    sms.get(key) shouldNot be (None)
    sms.get("notexisting") should be (None)
  }

  it should "delAll" in {
    sms.size should be (0)
    val key = sms.add(listing)
    val key2 = sms.add(listing)
    sms.size should be (2)
    val delall = sms.delAll
    sms.size should be (0)
    delall.foreach(x => Set(key, key2).contains(x.listing.id.get) should be (true))
  }

  it should "size" in {
    sms.size should be (0)
    sms.add(listing)
    sms.add(listing)
    val key = sms.add(listing)
    sms.size should be (3)
    sms.del(key)
    sms.size should be (2)
    sms.delAll
    sms.size should be (0)
  }

  it should "del" in {
    val key1 = sms.add(listing)
    val key2 = sms.add(listing)
    sms.size should be (2)
    val deleted1 = sms.del(key1)
    sms.size should be (1)
    deleted1.listing.id should be (Some(key1))
    val key3 = sms.add(listing)
    val deleted3 = sms.del(key3)
    deleted3.listing.id should be (Some(key3))
  }

  it should "add" in {
    sms.size should be (0)
    val key1 = sms.add(listing)
    val key2 = sms.add(listing)
    sms.size should be (2)
    val obj1 = sms.get(key1)
    obj1.get should be (listing.copy(listing = listing.listing.copy(id=Some(key1))))
  }

  it should "getAll" in {
    sms.size should be (0)
    val key1 = sms.add(listing)
    val key2 = sms.add(listing)
    sms.size should be (2)
    sms.getAll.foreach(x => Set(key1, key2).contains(x.listing.id.get) should be (true))
  }

}
