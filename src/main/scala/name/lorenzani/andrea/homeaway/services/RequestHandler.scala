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

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import name.lorenzani.andrea.homeaway.datastore.DataStore

trait RequestHandler {
  def handle(request: Request): Future[Response]
}

object RequestHandler {

  def getGetHandler: RequestHandler = new GetRequestHandler(DataStore.getDataStore)
  def getPostHandler: RequestHandler = new PostRequestHandler(DataStore.getDataStore)
  def getDelHandler: RequestHandler = new DelRequestHandler(DataStore.getDataStore)
  def getPutHandler: RequestHandler = new PutRequestHandler(DataStore.getDataStore)
}

