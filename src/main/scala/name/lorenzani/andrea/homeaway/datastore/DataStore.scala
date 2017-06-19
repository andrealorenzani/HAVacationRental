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

/*
* Example of json
* {
  "listing": {
    "id": "5e22a83a-6f4f-11e6-8b77-86f30ca893d3",
    "contact": {
      "phone": "15126841100",
      "formattedPhone": "+1 512-684-1100"
    },
    "address": {
      "address": "1011 W 5th St",
      "postalCode": "1011",
      "countryCode": "US",
      "city": "Austin",
      "state": "TX",
      "country": "United States"
    },
    "location": {
      "lat": 40.4255485534668,
      "lng": -3.7075681686401367
    }
  }
}

*
* */

sealed case class ListingWrapper(listing: Listing)
sealed case class Listing(id: Option[String], contact: Option[Contact], address: Address, location: Option[Location])
sealed case class Contact(phone: String, formattedPhone: Option[String])
sealed case class Address(address: String, postalCode: String, countryCode: Option[String], city: Option[String], state: Option[String], country: String)
sealed case class Location(lat: Double, lng: Double)
sealed case class Size(size: Int)

abstract class DataStore {
  def get(id: String): Option[ListingWrapper]
  def add(newListing: ListingWrapper): String
  def update(newListing: ListingWrapper): Unit
  def del(listingId: String): ListingWrapper

  // Not required but... nice to have for testing (even if delAll is dangerous, should be removed)
  def delAll: List[ListingWrapper]
  def getAll: List[ListingWrapper]
  def size: Int
}

object DataStore {
  private val ds = new SimpleMapStore
  def getDataStore: DataStore = ds
}