package name.lorenzani.andrea.homeaway.datastore

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

class SimpleMapStore extends DataStore{
  // I give a bit of breath to the amortized linear insert
  private val store = new ConcurrentHashMap[String, ListingWrapper](200)

  override def get(id: String): Option[ListingWrapper] = {
    if(store.containsKey(id)){
      Some(store.get(id))
    } else {
      None
    }
  }

  override def add(newListing: ListingWrapper): String = {
    require(newListing.listing.id.isEmpty, "The id of a new Listing should be assigned by the server. Maybe you want to use PUT?")
    val id = UUID.randomUUID().toString
    val listing = ListingWrapper(newListing.listing.copy(id=Some(id)))
    store.put(id, listing)
    id
  }

  override def update(newListing: ListingWrapper): Unit = {
    require(newListing.listing.id.isDefined, "The id of a listing cannot be empty. Maybe you want to use POST?")
    require(store.containsKey(newListing.listing.id.get), "The listing you are trying to update is not in the database")
    store.put(newListing.listing.id.get, newListing).listing.id
  }

  override def del(listingId: String): ListingWrapper = {
    require(store.containsKey(listingId), "Not found")
    store.remove(listingId)
  }

  override def delAll: List[ListingWrapper] = {
    val res = getAll
    store.clear()
    res
  }

  override def getAll: List[ListingWrapper] = {
    store.entrySet().asScala.map(x => x.getValue).toList
  }

  override def size: Int = store.size()
}
