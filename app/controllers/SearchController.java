/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elasticsearch.ElasticSearchClient;
import play.mvc.Controller;
import elasticsearch.Searchable;
import exceptions.NotSearchableException;
import java.util.LinkedList;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import play.Logger;
import play.Play;

/**
 *
 * @author judu
 */
public class SearchController extends Controller {

   public static void search(String query) {
      Client c = new ElasticSearchClient();

      QueryStringQueryBuilder qsqb = new QueryStringQueryBuilder(query);

      SearchResponse rayponce = c.prepareSearch(Play.configuration.getProperty("elasticsearch.indexname")).setSearchType(SearchType.DEFAULT).setQuery(qsqb).execute().actionGet();

      List<Searchable> result = new LinkedList<Searchable>();

      for (SearchHit searchHit : rayponce.hits()) {
         try {
            Object obj = Class.forName(searchHit.getType()).
                    getMethod("getFrom", new Class[]{SearchHit.class}).
                    invoke(Class.forName(searchHit.getType()), new Object[]{searchHit});
            if (obj instanceof Searchable) {
               Searchable sobj = (Searchable) obj;
               sobj.setScore(searchHit.getScore());
               result.add(sobj);
            } else {
               throw new NotSearchableException("The returned object is not a Searchable element.");
            }
         } catch (NotSearchableException e) {
            Logger.debug("Not searchable : %s", e.getMessage());
            error(503, "Encountered an internal server error");
         } catch (ClassNotFoundException ex) {
            Logger.debug("no CRUDFieldProvider found for %s", ex);
            error(503, "Encountered an internal server error");
         } catch (Exception e) {
            e.printStackTrace();
            error();
         }
      }

      render(result, query);
   }
}
