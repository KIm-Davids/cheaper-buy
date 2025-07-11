import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import com.packages.scraperapi.services.ComparePricesServicesServicesImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ComparePricesEndpointTest {

    @Test
    public void testToSeeIfJumiaApiCallResponds(){
        ComparePricesServicesServicesImpl services = new ComparePricesServicesServicesImpl();
        Query query  = new Query();
        query.setQuery("Iphone xsmax phone");
        ProductResult result = services.scrapeJumia(query);
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void testToSeeIfKongaApiCallResponds(){
        ComparePricesServicesServicesImpl services = new ComparePricesServicesServicesImpl();
        Query query  = new Query();
        query.setQuery("Iphone xsmax phone");
        List<ProductResult> result = services.scrapeJiji(query);
        System.out.println(result);
        assertNotNull(result);
    }
}


