package name.lorenzani.andrea.homeaway.datastore

/**
  * Created by andrea on 18/06/17.
  */
class SimpleMapStore extends DataStore {
  override def get(id: String): Option[ListingWrapper] = ???

  override def add(newListing: ListingWrapper): String = ???

  override def update(newListing: ListingWrapper): Boolean = ???

  override def del(listingId: String): Boolean = ???

  override def delAll: List[ListingWrapper] = ???

  override def getAll: List[ListingWrapper] = ???

  override def size: Int = ???
}
