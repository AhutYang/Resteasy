package org.jboss.resteasy.test.providers.priority;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityExceptionMapperAAA;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityExceptionMapperBBB;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityExceptionMapperCCC;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityFoo;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityFooParamConverter;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityFooParamConverterProviderAAA;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityFooParamConverterProviderBBB;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityFooParamConverterProviderCCC;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityResource;
import org.jboss.resteasy.test.providers.priority.resource.ProviderPriorityTestException;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter ExceptionMappers and ParamConverterProviders
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProviderPriorityProvidersInApplicationTest {
   
   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ProviderPriorityProvidersInApplicationTest.class.getSimpleName());
      war.addClasses(ProviderPriorityFoo.class, 
            ProviderPriorityFooParamConverter.class,
            ProviderPriorityTestException.class
            );
      List<Class<?>> singletons = new ArrayList<Class<?>>();
      singletons.add(ProviderPriorityExceptionMapperCCC.class);
      singletons.add(ProviderPriorityFooParamConverterProviderCCC.class);
      return TestUtil.finishContainerPrepare(war, null, singletons,
            ProviderPriorityResource.class, 
            ProviderPriorityExceptionMapperAAA.class,
            ProviderPriorityExceptionMapperBBB.class,
            ProviderPriorityExceptionMapperCCC.class,
            ProviderPriorityFooParamConverterProviderAAA.class,
            ProviderPriorityFooParamConverterProviderBBB.class,
            ProviderPriorityFooParamConverterProviderCCC.class
            );
   }

   @Before
   public void init() {
      client = new ResteasyClientBuilder().build();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ProviderPriorityProvidersInApplicationTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Tests that ExceptionMappers are sorted by priority
    * @tpSince RESTEasy 4.0.0
    */
//   @Test
   public void testExceptionMapper() throws Exception {
      WebTarget base = client.target(generateURL("/exception"));
      Response response = base.request().get();
      assertEquals(444, response.getStatus());
      assertEquals("CCC", response.readEntity(String.class));
   }
   
   /**
    * @tpTestDetails Tests that ParamConverterProviders are sorted by priority
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testParamConverterProvider() throws Exception {
      WebTarget base = client.target(generateURL("/paramconverter/dummy"));
      Response response = base.request().get();
      assertEquals(200, response.getStatus());
      assertEquals("CCC", response.readEntity(String.class));
   }
}
