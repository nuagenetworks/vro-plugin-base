package net.nuagenetworks.vro.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.nuagenetworks.bambou.annotation.RestEntity;
import net.nuagenetworks.vro.model.fetchers.BaseFetcher;

public class BaseModelHelperTest {

    @RestEntity(restName = "test", resourceName = "test")
    class TestObject extends BaseObject {
        private static final long serialVersionUID = 1L;

        private String id;

        public TestObject(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public BaseSession<?> getSession() {
            return null;
        }

        @Override
        public void setSession(BaseSession<?> session) {
        }

        public boolean equals(Object o) {
            TestObject obj = (TestObject) o;
            return obj.id.equals(id);
        }
    }

    class TestFetcher extends BaseFetcher<TestObject> {
        private static final long serialVersionUID = 1L;

        private String id;

        public TestFetcher(String id, TestObject parent) {
            super(parent, TestObject.class);

            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static String OBJECT_ID = "1";
    private static String OBJECT_TYPE = "Test";

    private static String FETCHER_ID = "1";
    private static String FETCHER_TYPE = "Fetcher";

    @Before
    public void clearModel() {
        BaseModelHelper.clearCache();
    }

    @Test
    public void testAddObject() {
        TestObject obj = new TestObject(OBJECT_ID);
        TestObject res = BaseModelHelper.addObject(OBJECT_TYPE, obj);
        Assert.assertTrue(res == obj);
    }

    @Test
    public void testGetObject() {
        TestObject obj = new TestObject(OBJECT_ID);
        BaseModelHelper.addObject(OBJECT_TYPE, obj);

        TestObject res = BaseModelHelper.getObject(OBJECT_TYPE, OBJECT_ID);
        Assert.assertTrue(res == obj);
    }

    @Test
    public void testGetObjectNotFound() {
        TestObject res = BaseModelHelper.getObject(OBJECT_TYPE, OBJECT_ID);
        Assert.assertTrue(res == null);
    }

    @Test
    public void testGetObjectWithObjectGarbageCollected() {
        {
            TestObject obj = new TestObject(OBJECT_ID);
            BaseModelHelper.addObject(OBJECT_TYPE, obj);
            obj = null;
        }

        Runtime.getRuntime().gc();

        TestObject res = BaseModelHelper.getObject(OBJECT_TYPE, OBJECT_ID);
        Assert.assertTrue(res == null);
    }

    @Test
    public void testAddFetcher() {
        TestObject parentObj = new TestObject(OBJECT_ID);
        TestFetcher fetcher = new TestFetcher(FETCHER_ID, parentObj);
        TestFetcher res = BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);
        Assert.assertTrue(res == fetcher);
    }

    @Test
    public void testGetFetcher() {
        TestObject parentObj = new TestObject(OBJECT_ID);
        TestFetcher fetcher = new TestFetcher(FETCHER_ID, parentObj);
        BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);

        TestFetcher res = BaseModelHelper.getFetcher(FETCHER_TYPE, FETCHER_ID);
        Assert.assertTrue(res == fetcher);
    }

    @Test
    public void testGetFetcherNotFound() {
        TestFetcher res = BaseModelHelper.getFetcher(FETCHER_TYPE, FETCHER_ID);
        Assert.assertTrue(res == null);
    }

    @Test
    public void testGetFetcherWithFetcherGarbargeCollected() {
        {
            TestObject parentObj = new TestObject(OBJECT_ID);
            TestFetcher fetcher = new TestFetcher(FETCHER_ID, parentObj);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);
            parentObj = null;
            fetcher = null;
        }

        Runtime.getRuntime().gc();

        TestFetcher res = BaseModelHelper.getFetcher(FETCHER_TYPE, FETCHER_ID);
        Assert.assertTrue(res == null);
    }

    @Test
    public void testAddFetcherObjects() {
        TestObject parentObj = new TestObject(OBJECT_ID);
        TestFetcher fetcher = new TestFetcher(FETCHER_ID, parentObj);
        TestObject childObj1 = new TestObject("11");
        TestObject childObj2 = new TestObject("12");
        fetcher.add(childObj1);
        fetcher.add(childObj2);
        BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);
        BaseModelHelper.addFetcherObjects(fetcher, OBJECT_TYPE);

        Assert.assertNotNull(BaseModelHelper.getObject(OBJECT_TYPE, "11"));
        Assert.assertNotNull(BaseModelHelper.getObject(OBJECT_TYPE, "12"));
    }

    @Test
    public void testAddFetcherObjectsWithChildGarbageCollected() {
        {
            TestObject parentObj = new TestObject(OBJECT_ID);
            TestFetcher fetcher = new TestFetcher(FETCHER_ID, parentObj);
            TestObject childObj1 = new TestObject("11");
            TestObject childObj2 = new TestObject("12");
            fetcher.add(childObj1);
            fetcher.add(childObj2);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);
            BaseModelHelper.addFetcherObjects(fetcher, OBJECT_TYPE);
            fetcher.remove(childObj1);
            childObj1 = null;
        }

        Runtime.getRuntime().gc();

        Assert.assertNull(BaseModelHelper.getObject(OBJECT_TYPE, "11"));
        Assert.assertNotNull(BaseModelHelper.getObject(OBJECT_TYPE, "12"));
    }

    @Test
    public void testGetFetchers() {
        TestObject parentObj = new TestObject(OBJECT_ID);
        TestFetcher fetcher1 = new TestFetcher("21", parentObj);
        BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher1);
        TestFetcher fetcher2 = new TestFetcher("22", parentObj);
        BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher2);

        List<TestFetcher> fetchers = BaseModelHelper.getFetchers(FETCHER_TYPE);
        Assert.assertEquals(2, fetchers.size());
    }

    @Test
    public void testGetFetchersNotFound() {
        Assert.assertEquals(0, BaseModelHelper.getFetchers(FETCHER_TYPE).size());
    }

    @Test
    public void testGetFetchersWithFetcherGarbageCollected() {
        {
            TestObject parentObj = new TestObject(OBJECT_ID);
            TestFetcher fetcher1 = new TestFetcher("21", parentObj);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher1);
            TestFetcher fetcher2 = new TestFetcher("22", parentObj);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher2);
            fetcher1 = null;
        }

        Runtime.getRuntime().gc();

        List<TestFetcher> fetchers = BaseModelHelper.getFetchers(FETCHER_TYPE);
        Assert.assertEquals(1, fetchers.size());
        Assert.assertEquals("22", fetchers.get(0).getId());
    }

    @Test
    public void testGetFetchersWithFetchersGarbageCollected() {
        {
            TestObject parentObj = new TestObject(OBJECT_ID);
            TestFetcher fetcher1 = new TestFetcher("21", parentObj);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher1);
            TestFetcher fetcher2 = new TestFetcher("22", parentObj);
            BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher2);
            fetcher1 = null;
            fetcher2 = null;
            parentObj = null;
        }

        Runtime.getRuntime().gc();

        List<TestFetcher> fetchers = BaseModelHelper.getFetchers(FETCHER_TYPE);
        Assert.assertEquals(0, fetchers.size());
    }

    @Test
    public void testGetObjectAndGetFetcherWithSameId() {
        TestObject obj = new TestObject("15");
        BaseModelHelper.addObject(OBJECT_TYPE, obj);

        TestObject parentObj = new TestObject(OBJECT_ID);
        TestFetcher fetcher = new TestFetcher("15", parentObj);
        BaseModelHelper.addFetcher(FETCHER_TYPE, fetcher);

        TestObject res1 = BaseModelHelper.getObject(OBJECT_TYPE, "15");
        Assert.assertTrue(res1 == obj);

        TestFetcher res2 = BaseModelHelper.getFetcher(FETCHER_TYPE, "15");
        Assert.assertTrue(res2 == fetcher);
    }
}
