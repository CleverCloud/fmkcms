package controllers;

import elasticsearch.ElasticSearchClient;
import play.mvc.Controller;
import elasticsearch.Searchable;
import exceptions.NotSearchableException;
import java.util.LinkedList;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import play.Logger;
import play.Play;

/**
 *
 * @author judu
 */
public class SearchViewer extends Controller {

   public static void search(String query) {
      renderArgs.put("results", SearchViewer.doSearch(query));
      renderArgs.put("query", query);
      render();
   }
   
   public static List<Searchable> doSearch(String query) {
      if (!query.isEmpty()) {
         Client c = new ElasticSearchClient();
         org.elasticsearch.index.query.QueryStringQueryBuilder qsqb = new org.elasticsearch.index.query.QueryStringQueryBuilder(query);

         SearchResponse rayponce = c.prepareSearch(Play.configuration.getProperty("elasticsearch.indexname")).setSearchType(SearchType.DEFAULT).setQuery(qsqb).execute().actionGet();

         List<Searchable> result = new LinkedList<Searchable>();

         for (SearchHit searchHit : rayponce.hits()) {
            try {
               
               Object obj = Class.forName(searchHit.getType()).
                       getMethod("getFrom", new Class[]{SearchHit.class}).
                       invoke(Class.forName(searchHit.getType()), new Object[]{searchHit});
                       
               if (obj != null) {
                  if (obj instanceof elasticsearch.Searchable) {
                     Searchable sobj = (Searchable) obj;
                     sobj.setScore(searchHit.getScore());
                     result.add(sobj);
                  } else {
                     throw new NotSearchableException("The returned object is not a Searchable element : " + searchHit.getType());
                  }
               }
            } catch (NotSearchableException e) {
               Logger.error("Not searchable : %s", e.getMessage());
            } catch (ClassNotFoundException ex) {
               Logger.error("no CRUDFieldProvider found for %s", ex);
            } catch (Exception e) {
               Logger.error("Unknown error : ", e.getMessage());
            }
         }
         
         return result;
      } else {
         return new LinkedList<Searchable>();
      }
   }
}
