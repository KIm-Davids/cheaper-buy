import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import com.packages.scraperapi.repository.ProductRepository;
import com.packages.scraperapi.services.ComparePricesServicesImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull; // ✅ keep this

@SpringBootTest
public class ComparePricesEndpointTest {

  private ComparePricesServicesImpl services;

  @Before
  public void setup(){
      ProductRepository mockRepo = Mockito.mock(ProductRepository.class);
      services = new ComparePricesServicesImpl(mockRepo);
  }


    @Test
    public void testToSeeIfJumiaApiCallResponds(){
        Query query  = new Query();
        query.setQuery("iphone xs max");
        query.setBudgetAmount(250000);
        List<ProductResult> result = services.scrapeJumia(query);
        System.out.println(result);
        org.junit.jupiter.api.Assertions.assertNotNull(result);

    }

    @Test
    public void testToSeeIfJijiApiCallResponds(){
        Query query  = new Query();
        query.setQuery("Iphone xsmax phone");
        query.setBudgetAmount(250000);
        List<ProductResult> result = services.scrapeJiji(query);
        System.out.println(result);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    public void testToSeeIfKongaApiCallResponds(){
        Query query  = new Query();
        query.setQuery("samsung");
        query.setBudgetAmount(250000);
        List<ProductResult> result = services.scrapeKonga(query);
        System.out.println(result);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    public void testToSeeIfKusnapApiCallResponds(){
        Query query  = new Query();
        query.setQuery("iphone xs max");
        query.setBudgetAmount(250000);
        List<ProductResult> result = services.scrapeKusnap(query);
        System.out.println(result);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    public void testToSeeIfTheFilterWorks(){
        Query query  = new Query();
        query.setQuery("iphone xs max");
        query.setBudgetAmount(250000);
        List<ProductResult> result = services.getFilteredProducts(query);
        System.out.println(result);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }
}


